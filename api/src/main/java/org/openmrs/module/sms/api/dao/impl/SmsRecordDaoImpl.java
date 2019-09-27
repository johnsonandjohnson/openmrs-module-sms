package org.openmrs.module.sms.api.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.module.sms.api.audit.SmsDirection;
import org.openmrs.module.sms.api.audit.SmsRecord;
import org.openmrs.module.sms.api.audit.SmsRecordSearchCriteria;
import org.openmrs.module.sms.api.dao.BaseOpenmrsDataDao;
import org.openmrs.module.sms.api.dao.SmsRecordDao;
import org.openmrs.module.sms.api.web.Interval;
import org.openmrs.module.sms.domain.PagingInfo;

import java.util.List;
import java.util.Set;

public class SmsRecordDaoImpl extends BaseOpenmrsDataDao<SmsRecord> implements SmsRecordDao {

    public SmsRecordDaoImpl() {
        super(SmsRecord.class);
    }

    @Override
    public List<SmsRecord> findByCriteria(String config, Set<SmsDirection> directions, String phoneNumber,
                                          String messageContent, Interval timestamp, Set<String> deliveryStatuses,
                                          String providerStatus, String motechId, String providerId, String errorMessage,
                                          Order order) {
        Criteria crit = getSession().createCriteria(this.mappedClass);
        crit.add(Restrictions.eq("config", config));
        crit.add(Restrictions.in("smsDirection", directions));
        crit.add(Restrictions.eq("phoneNumber", phoneNumber));
        crit.add(Restrictions.eq("messageContent", messageContent));
        crit.add(Restrictions.eq("timestamp", timestamp));
        crit.add(Restrictions.in("deliveryStatus", deliveryStatuses));
        crit.add(Restrictions.eq("providerStatus", providerStatus));
        crit.add(Restrictions.eq("motechId", motechId));
        crit.add(Restrictions.eq("providerId", providerId));
        crit.add(Restrictions.eq("errorMessage", errorMessage));
        crit.addOrder(order);

        return crit.list();
    }

    @Override
    public long countFindByCriteria(String config, Set<SmsDirection> directions, String phoneNumber, String messageContent,
                                    Interval timestamp, Set<String> deliveryStatuses, String providerStatus,
                                    String motechId, String providerId, String errorMessage) {
        Criteria crit = getSession().createCriteria(this.mappedClass);
        crit.add(Restrictions.eq("config", config));
        crit.add(Restrictions.in("smsDirection", directions));
        crit.add(Restrictions.eq("phoneNumber", phoneNumber));
        crit.add(Restrictions.eq("messageContent", messageContent));
        crit.add(Restrictions.eq("timeStamp", timestamp));
        crit.add(Restrictions.in("deliveryStatus", deliveryStatuses));
        crit.add(Restrictions.eq("providerStatus", providerStatus));
        crit.add(Restrictions.eq("motechId", motechId));
        crit.add(Restrictions.eq("providerId", providerId));
        crit.add(Restrictions.eq("errorMessage", errorMessage));

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
