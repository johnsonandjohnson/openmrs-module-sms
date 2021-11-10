package org.openmrs.module.sms.api.audit;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.openmrs.BaseOpenmrsData;

public abstract class AbstractBaseOpenmrsData extends BaseOpenmrsData {

  private static final long serialVersionUID = -2626463306839051522L;

  private static final String ID_FIELD_NAME = "id";

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    return EqualsBuilder.reflectionEquals(this, o, ID_FIELD_NAME);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this, ID_FIELD_NAME);
  }
}
