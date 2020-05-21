package com.xl.study.calcite.sql.parser;

import org.apache.calcite.adapter.java.ReflectiveSchema;
import org.apache.calcite.plan.RelOptUtil;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.RelRoot;
import org.apache.calcite.schema.SchemaPlus;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.tools.FrameworkConfig;
import org.apache.calcite.tools.Frameworks;
import org.apache.calcite.tools.Planner;
import org.junit.Test;

public class TestSqlParser
{
    public static class TestSchema {
        public final Triple[] rdf = {new Triple("s", "p", "o")};
    }

    @Test
    public void testSQL() {
        SchemaPlus schemaPlus = Frameworks.createRootSchema(true);
        
        //add Table in schema T
        schemaPlus.add("T", new ReflectiveSchema(new TestSchema()));
        Frameworks.ConfigBuilder configBuilder = Frameworks.newConfigBuilder();
        //set default schema
        configBuilder.defaultSchema(schemaPlus);
 
        FrameworkConfig frameworkConfig = configBuilder.build();
 
        SqlParser.ConfigBuilder paresrConfig = SqlParser.configBuilder(frameworkConfig.getParserConfig());
        
        // setCaseSensitive=false for SQL
        paresrConfig.setCaseSensitive(false).setConfig(paresrConfig.build());
 
        Planner planner = Frameworks.getPlanner(frameworkConfig);
 
        SqlNode sqlNode;
        RelRoot relRoot = null;
        try {
            //parser
            sqlNode = planner.parse("select \"a\".\"s\", count(\"a\".\"s\") from \"T\".\"rdf\" \"a\" group by \"a\".\"s\"");
            //validate
            planner.validate(sqlNode);
            //get RelNode root
            relRoot = planner.rel(sqlNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        RelNode relNode = relRoot.project();
        System.out.print(RelOptUtil.toString(relNode));
    }
    
}

