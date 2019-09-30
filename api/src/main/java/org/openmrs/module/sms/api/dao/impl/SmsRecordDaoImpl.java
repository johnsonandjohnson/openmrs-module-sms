package org.openmrs.module.sms.api.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.sms.api.audit.SmsRecord;
import org.openmrs.module.sms.api.audit.SmsRecordSearchCriteria;
import org.openmrs.module.sms.api.dao.BaseOpenmrsDataDao;
import org.openmrs.module.sms.api.dao.SmsRecordDao;
import org.openmrs.module.sms.domain.PagingInfo;

import java.util.List;

public class SmsRecordDaoImpl extends BaseOpenmrsDataDao<SmsRecord> implements SmsRecordDao {

    public SmsRecordDaoImpl() {
        super(SmsRecord.class);
    }

    @Override
    public List<SmsRecord> findByCriteria(SmsRecordSearchCriteria searchCriteria) {
        Criteria crit = getSession().createCriteria(this.mappedClass);
        searchCriteria.loadSearchCriteria(crit);

        return crit.list();
    }

    @Override
    public long countFindByCriteria(SmsRecordSearchCriteria searchCriteria) {
        Criteria crit = getSession().createCriteria(this.mappedClass);
        searchCriteria.loadSearchCriteria(crit);

        Number count = (Number) crit.uniqueResult();
        return count.longValue();
    }

    @Override
    public List<SmsRecord> findByProviderId(String providerId) {
       Criteria crit = getSession().createCriteria(this.mappedClass);
       crit.add(Restrictions.eq("providerId", providerId));

       return crit.list();
    }

    @Override
    public List<SmsRecord> findByMotechId(String motechId) {
        Criteria crit = getSession().createCriteria(this.mappedClass);
        crit.add(Restrictions.eq("motechId", motechId));

        return crit.list();
    }

    @Override
    public List<SmsRecord> findByProviderAndMotechId(String providerId, String motechId) {
        Criteria crit = getSession().createCriteria(this.mappedClass);
        crit.add(Restrictions.eq("providerId", providerId));
        crit.add(Restrictions.eq("motechId", motechId));

        return crit.list();
    }

    @Override
    public void deleteAll() {
        getSession().createQuery("delete from sms.smsRecord").executeUpdate();
    }

    @Override
    public SmsRecord create(SmsRecord smsRecord) {
        return saveOrUpdate(smsRecord);
    }

    @Override
    public List<SmsRecord> retrieveAll() {
        return getAll(false);
    }

    @Override
    public List<SmsRecord> findPageableByCriteria(PagingInfo pagingInfo, SmsRecordSearchCriteria searchCriteria) {
        Criteria crit = getSession().createCriteria(this.mappedClass);
        searchCriteria.loadSearchCriteria(crit);
        loadPagingTotal(pagingInfo, crit);
        createPagingCriteria(pagingInfo, crit);
        return crit.list();
    }
}
