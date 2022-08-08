package org.openmrs.module.sms.api.adhocsms;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityNotFoundException;
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
import org.springframework.transaction.annotation.Transactional;

public class AdHocSMSSQLDataSetProcessor implements AdHocSMSInputSourceProcessor {

  private static final Log LOGGER = LogFactory.getLog(AdHocSMSSQLDataSetProcessor.class);

  public static final String DATA_SET_UUID_PROP_NAME = "dataSetUuid";

  private DbSessionFactory dbSessionFactory;

  public AdHocSMSSQLDataSetProcessor(DbSessionFactory dbSessionFactory) {
    this.dbSessionFactory = dbSessionFactory;
  }

  @Override
  public boolean shouldProcessData(AdHocSMSInputSourceProcessorContext context) {
    return context.getOptions().containsKey(DATA_SET_UUID_PROP_NAME);
  }

  @Override
  @Transactional(readOnly = true)
  public List<AdHocSMSData> getAdHocSMSData(AdHocSMSInputSourceProcessorContext context) {
    String dataSetUuid = context.getOptions().get(DATA_SET_UUID_PROP_NAME);
    DataSetDefinition dataSet = Context.getService(DataSetDefinitionService.class)
        .getDefinitionByUuid(dataSetUuid);
    if (dataSet == null) {
      throw new EntityNotFoundException(
          String.format("Data set definition with uuid %s not found", dataSetUuid));
    }

    SQLQuery sqlQuery = getSQLQuery(dataSet);
    return getSMSData(sqlQuery.list());
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
      LOGGER.error("Error occurred while mapping query results into AdHocSMSData class", ex);
    }

    return resultList;
  }
}
