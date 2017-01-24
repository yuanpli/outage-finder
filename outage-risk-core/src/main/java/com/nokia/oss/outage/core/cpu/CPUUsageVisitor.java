package com.nokia.oss.outage.core.cpu;

import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.nokia.oss.outage.core.bean.RiskTypes;
import com.nokia.oss.outage.core.util.NodeTreeUtils;
import com.nokia.oss.outage.core.util.SourceCodeVisitorSupport;

/**
 * Created by harchen on 2015/12/7.
 */
public class CPUUsageVisitor
    extends SourceCodeVisitorSupport
{
    private RiskTypes riskTypes;

    public CPUUsageVisitor( RiskTypes riskTypes )
    {
        this.riskTypes = riskTypes;
    }

    @Override
    public void visit( WhileStmt statement, Object arg )
    {
        Expression condition = statement.getCondition();
        if( condition instanceof BooleanLiteralExpr )
        {
            BooleanLiteralExpr expr = (BooleanLiteralExpr)condition;
            if( expr.getValue() )
            {
                Statement body = statement.getBody();
                ReturnStmt returnStmt = NodeTreeUtils.findFirstSubNodeOfType( body, ReturnStmt.class );
                BreakStmt breakStmt = NodeTreeUtils.findFirstSubNodeOfType( body, BreakStmt.class );
                if( returnStmt == null && breakStmt == null )
                {
                    reportRisk( riskTypes.get( RiskTypes.OUTAGE_CPU_WHILE_TRUE_LOOP_WITHOUT_QUIT_STATEMENT), statement );
                }
            }
        }
        super.visit( statement, arg );
    }
}
