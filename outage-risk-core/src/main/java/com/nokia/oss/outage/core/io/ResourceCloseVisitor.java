package com.nokia.oss.outage.core.io;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.nokia.oss.outage.core.bean.RiskTypes;
import com.nokia.oss.outage.core.matcher.AssignmentExpressionMatcher;
import com.nokia.oss.outage.core.matcher.IOResourceChainMatcher;
import com.nokia.oss.outage.core.matcher.IOResourceVariableDeclarationMatcher;
import com.nokia.oss.outage.core.matcher.MethodCallOnVariableMatcher;
import com.nokia.oss.outage.core.matcher.MethodCallUsingVariableMatcher;
import com.nokia.oss.outage.core.matcher.NewIOResourceAsReturnValueMatcher;
import com.nokia.oss.outage.core.matcher.NodeMatcher;
import com.nokia.oss.outage.core.matcher.NodeTypeMatcher;
import com.nokia.oss.outage.core.matcher.VariableAsReturnValueMatcher;
import com.nokia.oss.outage.core.util.ClassUtils;
import com.nokia.oss.outage.core.util.Constants;
import com.nokia.oss.outage.core.util.NodeTreeUtils;
import com.nokia.oss.outage.core.util.SourceCodeVisitorSupport;

/**
 * Created by harchen on 2015/10/10.
 */
public class ResourceCloseVisitor
    extends SourceCodeVisitorSupport
{
    private static final Logger LOG = LoggerFactory.getLogger( ResourceCloseVisitor.class );
    private RiskTypes riskTypes;

    public ResourceCloseVisitor( RiskTypes riskTypes )
    {
        this.riskTypes = riskTypes;
    }

    @Override
    public void visit( InitializerDeclaration initializer, Object arg )
    {
        checkVariables( initializer.getBlock() );
    }

    @Override
    public void visit( MethodDeclaration methodDeclaration, Object arg )
    {
        checkMethodParameter( methodDeclaration );
        BlockStmt body = methodDeclaration.getBody();
        checkVariables( body );
        checkReturns( body );
    }

    private void checkMethodParameter( MethodDeclaration method )
    {
        List<Parameter> parameters = method.getParameters();
        if( parameters != null )
        {
            for( Parameter parameter : parameters )
            {
                Class type = ClassUtils.getPossibleFullClassName( parameter.getType().toString(), getImportList() );
                if( ClassUtils.isRiskyIOType( type ) )
                {
                    String name = parameter.getId().getName();
                    List<MethodCallExpr> closingMethodCalls =
                        checkResourceVariableClosingInFinallyBlock( method.getBody(), name );
                    if( closingMethodCalls.isEmpty() )
                    {
                        reportRisk( riskTypes.get( RiskTypes.OUTAGE_IO_RESOURCE_AS_METHOD_INPUT_PARAMETER), parameter );
                    }
                }
            }
        }
    }

    private void checkVariables( BlockStmt body )
    {
        List<VariableDeclarator> variables = getIOResourceTypeVariables( body );
        checkVariableAssignment( body, variables );
        if( !variables.isEmpty() )
        {
            checkResourceClosing( body, variables );
        }
    }

    private void checkVariableAssignment( BlockStmt body, List<VariableDeclarator> variables )
    {
        Iterator<VariableDeclarator> iterator = variables.iterator();
        while( iterator.hasNext() )
        {
            VariableDeclarator variable = iterator.next();
            if( !isRiskyTypeAssignment( body, variable ) )
            {
                iterator.remove();
            }
        }
    }

    private boolean isRiskyTypeAssignment( BlockStmt body, VariableDeclarator variable )
    {
        boolean isRisky = true;
        NodeMatcher matcher = new AssignmentExpressionMatcher( variable.getId().getName() );
        List<AssignExpr> assignments = NodeTreeUtils.findAllSubNode( body, matcher );
        if( assignments.size() == 0 )
        {
            isRisky = isInitWithRiskyType( variable );
        }
        else if( assignments.size() == 1 )
        {
            isRisky = isAssignedTypeRisky( assignments.get( 0 ) );
        }
        else
        {
            AssignExpr assignment = NodeTreeUtils.lastOf( assignments );
            isRisky = isAssignedTypeRisky( assignment );
        }
        return isRisky;
    }

    private boolean isInitWithRiskyType( VariableDeclarator variable )
    {
        boolean isRisky = true;
        Expression init = variable.getInit();
        if( init != null )
        {
            if( init instanceof ObjectCreationExpr )
            {
                isRisky = isRiskyObjectCreation( (ObjectCreationExpr)init );
            }
            else if( init instanceof MethodCallExpr )
            {
                isRisky = !isCreatedByServlet( (MethodCallExpr)init );
            }
        }
        return isRisky;
    }

    private boolean isCreatedByServlet( MethodCallExpr methodCall )
    {
        boolean isCreatedByServlet = false;
        MethodDeclaration method = NodeTreeUtils.findFirstEnclosingNodeOfType( methodCall, MethodDeclaration.class );
        Expression expression = methodCall.getScope();
        if( expression instanceof NameExpr)
        {
            NameExpr nameExpr = (NameExpr)expression;
            String name = nameExpr.getName();
            Type variableType = getVariableType( method, name );
            if( variableType != null && isServletType( variableType ) )
            {
                isCreatedByServlet = true;
            }
        }
        return isCreatedByServlet;
    }

    private Type getVariableType( MethodDeclaration method, String name )
    {
        Type variableType = null;
        VariableDeclarationExpr variableDeclarationExpr = findDeclaredLocalVariable( method, name );
        if( variableDeclarationExpr != null )
        {
            variableType = variableDeclarationExpr.getType();
        }
        else
        {
            for( Parameter parameter : method.getParameters() )
            {
                if( parameter.getId().getName().equals( name ) )
                {
                    variableType = parameter.getType();
                }
            }
        }
        return variableType;
    }

    private VariableDeclarationExpr findDeclaredLocalVariable(MethodDeclaration method, String name )
    {
        VariableDeclarationExpr variableDeclarationExpr = null;
        NodeMatcher matcher = new NodeTypeMatcher( VariableDeclarationExpr.class );
        List<VariableDeclarationExpr> variableDeclarations = NodeTreeUtils.findAllSubNode( method.getBody(), matcher );
        for( VariableDeclarationExpr variableDeclaration : variableDeclarations )
        {
            for( VariableDeclarator variableDeclarator : variableDeclaration.getVars() )
            {
                if( variableDeclarator.getId().getName().equals( name ) )
                {
                    if( isServletType( variableDeclaration.getType() ) )
                    {
                        variableDeclarationExpr = variableDeclaration;
                        break;
                    }
                }
            }
            if( variableDeclaration != null )
            {
                break;
            }
        }
        return variableDeclarationExpr;
    }

    private boolean isServletType( Type type )
    {
        boolean isCreatedByServlet = false;
        if( type instanceof ReferenceType )
        {
            Type referenceType = ((ReferenceType)type).getType();
            if( referenceType instanceof ClassOrInterfaceType )
            {
                ClassOrInterfaceType classOrInterfaceType = (ClassOrInterfaceType)referenceType;
                if( classOrInterfaceType.getName().equals( "HttpServletRequest" ) ||
                    classOrInterfaceType.getName().equals( "javax.servlet.http.HttpServletRequest" ) ||
                    classOrInterfaceType.getName().equals( "HttpServletResponse" ) ||
                    classOrInterfaceType.getName().equals( "javax.servlet.http.HttpServletResponse" ) )
                {
                    isCreatedByServlet = true;
                }
            }
        }
        return isCreatedByServlet;
    }

    private boolean isAssignedTypeRisky( AssignExpr assignment )
    {
        boolean isRisky = true;
        Expression value = assignment.getValue();
        if( value != null && value instanceof ObjectCreationExpr)
        {
            isRisky = isRiskyObjectCreation( (ObjectCreationExpr)value );
        }
        return isRisky;
    }

    private boolean isRiskyObjectCreation( ObjectCreationExpr value )
    {
        String typeName = value.getType().getName();
        Class type = ClassUtils.getPossibleFullClassName( typeName, getImportList() );
        if( type == null || ClassUtils.isSubTypeOfAny( type, Constants.IGNORED_IO_TYPES ) )
        {
            return false;
        }
        if( ClassUtils.isSubTypeOfAny( type, Constants.IO_TYPES ) )
        {
            List<Expression> args = value.getArgs();
            if( args == null )
            {
                return true;
            }
            for( Expression arg : args )
            {
                if( arg instanceof ObjectCreationExpr )
                {
                    return isRiskyObjectCreation( (ObjectCreationExpr)arg );
                }
            }

        }
        return true;
    }

    private void checkReturns( BlockStmt body )
    {
        NodeMatcher nodeMatcher = new NewIOResourceAsReturnValueMatcher( getImportList() );
        List<ReturnStmt> returnStatements = NodeTreeUtils.findAllSubNode( body, nodeMatcher );
        for( ReturnStmt statement : returnStatements )
        {
            reportRisk( riskTypes.get( RiskTypes.OUTAGE_IO_RESOURCE_AS_RETURN_VALUE), statement );
        }
    }

    private void checkResourceClosing( BlockStmt body, List<VariableDeclarator> variables )
    {
        List<MethodCallExpr> methodCalls = new ArrayList<MethodCallExpr>();
        for( VariableDeclarator variable : variables )
        {
            String variableName = variable.getId().getName();
            if( isVariableAsReturnValue( body, variableName ) )
            {
                reportRisk( riskTypes.get( RiskTypes.OUTAGE_IO_RESOURCE_AS_RETURN_VALUE), variable );
                continue;
            }
            List<MethodCallExpr> closingMethodList = getResourceVariableClosingMethodCalls( body, variableName );
            if( closingMethodList.isEmpty() )
            {
                checkVariableWithoutCloseMethodCall( body, variable );
            }
            else
            {
                methodCalls.addAll( checkVariableWithCloseMethodCall( body, variable, closingMethodList ) );
            }
        }
        checkResourceClosingImpactWithEachOther( methodCalls );
    }

    private List<MethodCallExpr> checkVariableWithCloseMethodCall(
        BlockStmt body,
        VariableDeclarator variable,
        List<MethodCallExpr> closingMethodList )
    {
        List<MethodCallExpr> methodCallInFinally = new ArrayList<MethodCallExpr>();
        String variableName = variable.getId().getName();
        if( isClosingMethodTheFirstMethodAfterDeclaration( body, closingMethodList, variable ) )
        {
            LOG.debug( "There is no method call before the closing of " + variableName + " , seem to be secure!" );
        }
        else
        {
            List<MethodCallExpr> methodCalls = checkResourceVariableClosingInFinallyBlock( body, variableName );
            if( methodCalls.isEmpty() )
            {
                reportRisk( riskTypes.get( RiskTypes.OUTAGE_IO_UNCLOSED_RESOURCE), variable );
            }
            else
            {
                methodCallInFinally.addAll( methodCalls );
            }
        }
        return methodCallInFinally;
    }

    private void checkVariableWithoutCloseMethodCall( BlockStmt body, VariableDeclarator variable )
    {
        String variableName = variable.getId().getName();
        if( isVariablePassedIntoMethod( body, variableName ) )
        {
            reportRisk( riskTypes.get( RiskTypes.OUTAGE_IO_RESOURCE_PASSED_TO_METHOD_AS_PARAMETER), variable );
        }
        else if( isVariablePassedIntoIOChain( body, variableName ) )
        {
            LOG.debug( "variable " + variableName + " is passed into IO resource chain, ignore it!" );
        }
        else
        {
            reportRisk( riskTypes.get( RiskTypes.OUTAGE_IO_UNCLOSED_RESOURCE), variable );
        }
    }

    private boolean isClosingMethodTheFirstMethodAfterDeclaration(
        BlockStmt body,
        List<MethodCallExpr> closingMethodList,
        VariableDeclarator variable )
    {
        int methodCallCount = 0;
        NodeMatcher matcher = new NodeTypeMatcher( MethodCallExpr.class );
        List<MethodCallExpr> allMethodsCalls = NodeTreeUtils.findAllSubNode( body, matcher );
        for( MethodCallExpr closingMethodCall : closingMethodList )
        {
            for( MethodCallExpr methodCall : allMethodsCalls )
            {
                if( methodCall.getBeginLine() < closingMethodCall.getBeginLine() &&
                    methodCall.getBeginLine() > variable.getEndLine() )
                {
                    methodCallCount++;
                }
            }
        }
        return methodCallCount == 0;
    }

    private boolean isVariablePassedIntoIOChain( BlockStmt body, String variableName )
    {
        NodeMatcher matcher = new IOResourceChainMatcher( variableName, getImportList() );
        List<ObjectCreationExpr> statements = NodeTreeUtils.findAllSubNode( body, matcher );
        return !statements.isEmpty();
    }

    private boolean isVariableAsReturnValue( BlockStmt body, String variableName )
    {
        VariableAsReturnValueMatcher matcher = new VariableAsReturnValueMatcher( variableName );
        List<ReturnStmt> statements = NodeTreeUtils.findAllSubNode( body, matcher );
        return !statements.isEmpty();
    }

    private boolean isVariablePassedIntoMethod( BlockStmt body, String variableName )
    {
        MethodCallUsingVariableMatcher matcher = new MethodCallUsingVariableMatcher( variableName );
        List<MethodCallExpr> statements = NodeTreeUtils.findAllSubNode( body, matcher );
        return !statements.isEmpty();
    }

    private List<MethodCallExpr> getResourceVariableClosingMethodCalls( BlockStmt body, String variableName )
    {
        List<MethodCallExpr> methodCalls = new LinkedList<MethodCallExpr>();
        methodCalls.addAll( findAllMethodCallOnVariableWithinNode( body, variableName, Constants.IO_CLOSE_METHOD ) );
        for( String utilityMethod : Constants.IO_CLOSE_UTILITY_METHOD )
        {
            methodCalls.addAll( findAllMethodCallUsingVariableWithinNode( body, utilityMethod, variableName ) );
        }
        return methodCalls;
    }

    private List<MethodCallExpr> checkResourceVariableClosingInFinallyBlock( BlockStmt body, String variableName )
    {
        List<MethodCallExpr> calls = new LinkedList<MethodCallExpr>();
        List<BlockStmt> finallyBlocks = getFinallyBlocks( body );
        for( BlockStmt block : finallyBlocks )
        {
            calls.addAll( findAllMethodCallOnVariableWithinNode( block, variableName, Constants.IO_CLOSE_METHOD ) );
            for( String utilityMethod : Constants.IO_CLOSE_UTILITY_METHOD )
            {
                calls.addAll( findAllMethodCallUsingVariableWithinNode( block, utilityMethod, variableName ) );
            }
        }
        return calls;
    }

    private void checkResourceClosingImpactWithEachOther( List<MethodCallExpr> resourceCloseMethodCall )
    {
        for( int i = 0; i < resourceCloseMethodCall.size(); i++ )
        {
            MethodCallExpr thisCall = resourceCloseMethodCall.get( i );
            if( isIOClosingUtilityMethodCall( thisCall ) )
            {
                continue;
            }
            for( int j = i + 1; j < resourceCloseMethodCall.size(); j++ )
            {
                MethodCallExpr thatCall = resourceCloseMethodCall.get( j );
                Node enclosingTryOfThis = getUpperLevelNodeOfType( thisCall, TryStmt.class );
                Node enclosingTryOfThat = getUpperLevelNodeOfType( thatCall, TryStmt.class );
                if( enclosingTryOfThis.equals( enclosingTryOfThat ) )
                {
                    MethodCallExpr node = getTheSecondMethodCall( thisCall, thatCall );
                    reportRisk( riskTypes.get( RiskTypes.OUTAGE_IO_RESOURCE_MAY_FAIL_TO_CLOSE), node );
                }
            }

        }
    }

    private boolean isIOClosingUtilityMethodCall( MethodCallExpr thisCall )
    {
        boolean result = false;
        Expression scope = thisCall.getScope();
        if( scope != null )
        {
            String call = scope.toString() + '.' + thisCall.getName();
            for( String utilityMethodCall : Constants.IO_CLOSE_UTILITY_METHOD )
            {
                if( utilityMethodCall.equals( call ) )
                {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    private MethodCallExpr getTheSecondMethodCall( MethodCallExpr thisCall, MethodCallExpr thatCall )
    {
        return thisCall.getBeginLine() > thatCall.getBeginLine() ? thisCall : thatCall;
    }

    private List<MethodCallExpr> findAllMethodCallOnVariableWithinNode( Node node, String variableName, String method )
    {
        NodeMatcher matcher = new MethodCallOnVariableMatcher( variableName, method );
        return NodeTreeUtils.findAllSubNode( node, matcher );
    }

    private List<MethodCallExpr> findAllMethodCallUsingVariableWithinNode(
        Node node,
        String methodName,
        String variableName )
    {
        MethodCallUsingVariableMatcher matcher = new MethodCallUsingVariableMatcher( methodName, variableName );
        return NodeTreeUtils.findAllSubNode( node, matcher );
    }

    private List<BlockStmt> getFinallyBlocks( BlockStmt body )
    {
        NodeMatcher matcher = new NodeTypeMatcher( TryStmt.class );
        List<TryStmt> tryBlocks = NodeTreeUtils.findAllSubNode( body, matcher );
        List<BlockStmt> finallyBlocks = new ArrayList<BlockStmt>();
        for( TryStmt block : tryBlocks )
        {
            BlockStmt finallyBlock = block.getFinallyBlock();
            if( finallyBlock != null )
            {
                finallyBlocks.add( finallyBlock );
            }
        }
        return finallyBlocks;
    }

    private List<VariableDeclarator> getIOResourceTypeVariables( BlockStmt body )
    {
        NodeMatcher matcher = new IOResourceVariableDeclarationMatcher( getImportList() );
        List<VariableDeclarationExpr> variableDeclarations = NodeTreeUtils.findAllSubNode( body, matcher );
        List<VariableDeclarator> variables = new ArrayList<VariableDeclarator>();
        for( VariableDeclarationExpr expr : variableDeclarations )
        {
            variables.addAll( expr.getVars() );
        }
        return variables;
    }

    public void setRiskTypes( RiskTypes riskTypes )
    {
        this.riskTypes = riskTypes;
    }
}
