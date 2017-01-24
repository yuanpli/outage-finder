package com.nokia.oss.outage.core.util;

import com.github.javaparser.ast.ImportDeclaration;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by harchen on 2015/10/13.
 */
public class ClassUtils
{
    public static final char DOT = '.';

    private static final ConcurrentHashMap<String, Class> classCache = new ConcurrentHashMap<String, Class>();

    public static Class lookForClass( String fullTypeName )
    {
        Class type = classCache.get( fullTypeName );
        if( type == null )
        {
            try
            {
                type = Class.forName( fullTypeName );
            }
            catch( ClassNotFoundException ignored )
            {

            }
            if( type != null )
            {
                classCache.putIfAbsent( fullTypeName, type );
            }
        }
        return type;
    }

    public static boolean isRiskyIOType( Class type )
    {
        return isSubTypeOfAny( type, Constants.IO_TYPES ) && !isSubTypeOfAny( type, Constants.IGNORED_IO_TYPES );
    }

    public static boolean isSubTypeOfAny( Class clazz, Class... types )
    {
        boolean result = false;
        if( clazz != null )
        {
            for( Class type : types )
            {
                if( type.isAssignableFrom( clazz ) )
                {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    public static Class getPossibleFullClassName( String typeName, List<ImportDeclaration> importList )
    {
        Class type = getImportedType( typeName, importList );
        if( type == null )
        {
            String fullTypeName = "java.lang." + typeName;
            type = ClassUtils.lookForClass( fullTypeName );
        }
        return type;
    }

    private static Class getImportedType( String typeName, List<ImportDeclaration> importList )
    {
        Class type = null;
        String fullTypeName = typeName;
        for( ImportDeclaration importDeclaration : importList )
        {
            String importName = importDeclaration.getName().toString();
            if( importDeclaration.isAsterisk() )
            {
                fullTypeName = importName + DOT + typeName;
            }
            else
            {
                if( importName.endsWith( DOT + typeName ) )
                {
                    fullTypeName = importName;
                }
            }
            type = ClassUtils.lookForClass( fullTypeName );
            if( type != null )
            {
                break;
            }

        }
        return type;
    }
}
