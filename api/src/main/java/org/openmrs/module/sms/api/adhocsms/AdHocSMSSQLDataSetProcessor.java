package org.openmrs.module.sms.api.adhocsms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.service.DataSetDefinitionService;
import org.openmrs.module.sms.api.data.AdHocSMSData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdHocSMSSQLDataSetProcessor {

  private static final Log LOGGER = LogFactory.getLog(AdHocSMSSQLDataSetProcessor.class);

  private DbSessionFactory dbSessionFactory;

  public AdHocSMSSQLDataSetProcessor(DbSessionFactory dbSessionFactory) {
    this.dbSessionFactory = dbSessionFactory;
  }

  public List<AdHocSMSData> getSMSDataFromSQLDataSet(String dataSetUuid) {
    DataSetDefinition dataSet = Context.getService(DataSetDefinitionService.class)
        .getDefinitionByUuid(dataSetUuid);
    List<AdHocSMSData> resultList = new ArrayList<>();
    if (dataSet != null) {
      SQLQuery sqlQuery = getSQLQuery(dataSet);
      resultList = getSMSData(sqlQuery.list());
    }

    return resultList;
  }

  public void setDbSessionFactory(DbSessionFactory dbSessionFactory) {
    this.dbSessionFactory = dbSessionFactory;
  }

  private SQLQuery getSQLQuery(DataSetDefinition dataSet) {
    SqlDataSetDefinition sqlDataSetDefinition = (SqlDataSetDefinition) dataSet;
    SQLQuery sqlQuery = dbSessionFactory.getCurrentSession()
        .createSQLQuery(sqlDataSetDefinition.getSqlQuery());
    sqlQuery.setResultTransformer((Transformers.ALIAS_TO_ENTITY_MAP));

    return sqlQuery;
  }

  private List<AdHocSMSData> getSMSData(List<Object> queryResults) {
    List<AdHocSMSData> resultList = new ArrayList<>();
    try {
      for (Object object : queryResults) {
        resultList.add(new AdHocSMSData((Map<String, String>) object));
      }
    } catch (IOException ex) {
      LOGGER.error("Error occurred while mapping query results into AdHocSMSData class");
    }

    return resultList;
  }
}
