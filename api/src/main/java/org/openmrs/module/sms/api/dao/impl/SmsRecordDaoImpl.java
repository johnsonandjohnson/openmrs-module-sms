package org.openmrs.module.sms.api.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.api.db.hibernate.HibernateOpenmrsDataDAO;
import org.openmrs.module.sms.api.audit.SmsDirection;
import org.openmrs.module.sms.api.audit.SmsRecord;
import org.openmrs.module.sms.api.dao.SmsRecordDao;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

public class SmsRecordDaoImpl extends HibernateOpenmrsDataDAO<SmsRecord> implements SmsRecordDao {

    @Autowired
    private DbSessionFactory sessionFactory;

    public SmsRecordDaoImpl() {
        super(SmsRecord.class);
    }

    private DbSession getSession(){
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<SmsRecord> findByCriteria(String config, Set<SmsDirection> directions, String phoneNumber,
                                          String messageContent, Range<DateTime> timestamp, Set<String> deliveryStatuses,
                                          String providerStatus, String motechId, String providerId, String errorMessage,
                                          Order order) {
        Criteria crit = getSession().createCriteria(this.mappedClass);
        crit.add(Restrictions.eq("config", config));
        crit.add(Restrictions.eq("smsDirection", directions));
        crit.add(Restrictions.eq("phoneNumber", phoneNumber));
        crit.add(Restrictions.eq("messageContent", messageContent));
        crit.add(Restrictions.eq("timestamp", timestamp));
        crit.add(Restrictions.eq("deliveryStatus", deliveryStatuses));
        crit.add(Restrictions.eq("providerStatus", providerStatus));
        crit.add(Restrictions.eq("motechId", motechId));
        crit.add(Restrictions.eq("providerId", providerId));
        crit.add(Restrictions.eq("errorMessage", errorMessage));
        crit.addOrder(order);

        return crit.list();
    }

    @Override
    public long countFindByCriteria(String config, Set<SmsDirection> directions, String phoneNumber, String messageContent,
                                    Range<DateTime> timestamp, Set<String> deliveryStatuses, String providerStatus,
                                    String motechId, String providerId, String errorMessage) {
        Criteria crit = getSession().createCriteria(this.mappedClass);
        crit.add(Restrictions.eq("config", config));
        crit.add(Restrictions.eq("smsDirection", directions));
        crit.add(Restrictions.eq("phoneNumber", phoneNumber));
        crit.add(Restrictions.eq("messageContent", messageContent));
        crit.add(Restrictions.eq("timeStamp", timestamp));
        crit.add(Restrictions.eq("deliveryStatus", deliveryStatuses));
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
}
