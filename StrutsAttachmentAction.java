package com.cenmobile.powerem.base;

import com.cenmobile.powerem.dao.entity.AttachmentEntity;
import com.cenmobile.powerem.dao.entity.attachement.EMAttachment;
import com.cenmobile.powerem.exception.JSONException;
import com.cenmobile.powerem.utils.JSONUtil;
import com.cenmobile.powerem.utils.RestRequestClient;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

public abstract class StrutsAttachmentAction<T extends AttachmentEntity> extends StrutsBaseAction<T>
{
  private static final long serialVersionUID = 6795402972557166695L;
  private String attachmentId;
  private String attachmentDeletedId;

  public String getAttachmentId()
  {
    return this.attachmentId;
  }

  public void setAttachmentId(String attachmentId) {
    this.attachmentId = attachmentId;
  }

  public String getAttachmentDeletedId() {
    return this.attachmentDeletedId;
  }

  public void setAttachmentDeletedId(String attachmentDeletedId) {
    this.attachmentDeletedId = attachmentDeletedId;
  }

  public String saveOrUpdateEntity(AttachmentEntity entity)
    throws JSONException
  {
    String businessId = null;
    AttachmentEntity attachmentEntity = entity;
    attachmentEntity.setAttachementIds(this.attachmentId);
    attachmentEntity.setDeletedAttachementIds(this.attachmentDeletedId);
    businessId = super.saveOrUpdateEntity(entity);
    return businessId;
  }

  public void deleteEntity()
  {
    super.deleteEntity();
  }

  protected void updateBusinessId4Attachment(String businessId, String businessType)
  {
    if ((StringUtils.isNotBlank(this.attachmentId)) || (StringUtils.isNotBlank(this.attachmentDeletedId))) {
      Map map = new HashMap();
      map.put("attachmentId", this.attachmentId);
      map.put("attachmentDeletedId", this.attachmentDeletedId);
      map.put("businessType", businessType);
      RestRequestClient.getInstance().putRestRequestWithParamter(EMAttachment.class.getSimpleName() + "/updateBusinessId/" + businessId, JSONUtil.serialize(map));
    }
  }
}