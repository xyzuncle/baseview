package com.cenmobile.powerem.rest.resource;

import com.cenmobile.powerem.dao.entity.DefaultEntity;
import com.cenmobile.powerem.dto.ReturnMessageDTO;
import com.cenmobile.powerem.exception.JSONException;
import com.cenmobile.powerem.model.IQueryCondition;
import com.cenmobile.powerem.model.IQueryConditionImpl;
import com.cenmobile.powerem.model.IResultSet;
import com.cenmobile.powerem.model.ObjectFactory;
import com.cenmobile.powerem.service.BaseService;
import com.cenmobile.powerem.utils.JSONUtil;
import com.cenmobile.powerem.utils.ReflectionUtils;
import com.cenmobile.powerem.utils.ThreadLocalUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class BaseResource<T extends DefaultEntity>
{
  protected Class<T> entityClass;
  
  public BaseResource()
  {
    this.entityClass = ((Class)ReflectionUtils.getSuperClassGenricTypes(getClass()).get(0));
  }
  
  protected abstract BaseService getDefaultService();
  
  public String returnSuccessMessage()
  {
    return returnSuccessMessage(null);
  }
  
  public String returnSuccessMessageById(String entityId)
  {
    Map<String, Object> returnMap = new HashMap();
    returnMap.put("result", entityId);
    returnMap.put("id", entityId);
    return returnSuccessMessage(returnMap);
  }
  
  public String returnSuccessMessage(Map<String, Object> info)
  {
    return returnResultMessage(0, "OK", info);
  }
  
  public String returnResultMessage(int resultCode, String resultMessage, Map<String, Object> info)
  {
    ReturnMessageDTO returnMsg = new ReturnMessageDTO();
    returnMsg.setCode(resultCode);
    returnMsg.setInfo(info);
    returnMsg.setMessage(resultMessage);
    try
    {
      return JSONUtil.serialize(returnMsg);
    }
    catch (JSONException e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  /**
   * 保存实体
   * @param postContent
   * @return
   */
  public String saveEntity(String postContent)
  {
    ThreadLocalUtils.putServiceOperation(ThreadLocalUtils.SERVICE_OPERATION_ADD);
    try
    {
      DefaultEntity entity = (DefaultEntity)JSONUtil.deserializeBeanDeep(postContent, this.entityClass);
      String entityId = getDefaultService().save(entity);
      return returnSuccessMessageById(entityId);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      Map<String, Object> returnMap = new HashMap();
      returnMap.put("errorMessage:", e.getMessage());
      return returnResultMessage(-999, e.getMessage(), returnMap);
    }
  }
  
  /**
   * 检查实体是否存在
   * @param entityId
   * @return
   */
  public boolean isEntityExisting(String entityId)
  {
    return getDefaultService().exist(entityId);
  }
  
  /**
   * 多条件查询
   * @param conditions
   * @return
   */
  public String getEntityByCondition(String conditions)
  {
    Map<String, Object> returnMap = new HashMap();
    try
    {
      IQueryCondition queryCondition = ObjectFactory.createQueryCondition();
      if (conditions != null) {
        queryCondition = (IQueryCondition)JSONUtil.deserializeBeanDeep(conditions, IQueryConditionImpl.class);
      }
      IResultSet resultSet = getDefaultService().query(queryCondition);
      returnMap.put("result", resultSet);
      return returnSuccessMessage(returnMap);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      returnMap.put("errorMessage:", e.getMessage());
      return returnResultMessage(-999, e.getMessage(), returnMap);
    }
  }
  
  /**
   * 删除实体
   * @param entityId
   * @return
   */
  public String deleteEntity(String entityId)
  {
    ThreadLocalUtils.putServiceOperation(ThreadLocalUtils.SERVICE_OPERATION_DELETE);
    Map<String, Object> returnMap = new HashMap();
    try
    {
      getDefaultService().delete(entityId);
      return returnSuccessMessage(returnMap);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      returnMap.put("errorMessage:", e.getMessage());
      return returnResultMessage(-999, e.getMessage(), returnMap);
    }
  }
  
  /**
   * 批量更新
   * @param defaultEntitys
   * @return
   */
  public String updateBatch(Set<DefaultEntity> defaultEntitys)
  {
    ThreadLocalUtils.putServiceOperation(ThreadLocalUtils.SERVICE_OPERATION_UPDATE);
    Map<String, Object> returnMap = new HashMap();
    try
    {
      getDefaultService().updateBatch(defaultEntitys);
      return returnSuccessMessage(returnMap);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      returnMap.put("errorMessage:", e.getMessage());
      return returnResultMessage(-999, e.getMessage(), returnMap);
    }
  }
  
  /**
   * 批量保存
   * @param defaultEntitys
   * @return
   */
  public String saveBatch(Set<DefaultEntity> defaultEntitys)
  {
    ThreadLocalUtils.putServiceOperation(ThreadLocalUtils.SERVICE_OPERATION_ADD);
    Map<String, Object> returnMap = new HashMap();
    try
    {
      getDefaultService().saveBatch(defaultEntitys);
      return returnSuccessMessage(returnMap);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      returnMap.put("errorMessage:", e.getMessage());
      return returnResultMessage(-999, e.getMessage(), returnMap);
    }
  }
  /**
   * 更新实体
   * @param entityId
   * @param content
   * @return
   */
  public String updateEntity(String entityId, String content)
  {
    ThreadLocalUtils.putServiceOperation(ThreadLocalUtils.SERVICE_OPERATION_UPDATE);
    Map<String, Object> returnMap = new HashMap();
    try
    {
      DefaultEntity defaultEntity = (DefaultEntity)JSONUtil.deserializeBeanDeep(content, this.entityClass);
      String id = getDefaultService().update(defaultEntity);
      returnMap.put("result", id);
      return returnSuccessMessage(returnMap);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      returnMap.put("errorMessage:", e.getMessage());
      return returnResultMessage(-999, e.getMessage(), returnMap);
    }
  }
  /**
   * 根据ID得到实体
   * @param entityId
   * @return
   */
  public String getEntitybyID(String entityId)
  {
    Map<String, Object> returnMap = new HashMap();
    try
    {
      DefaultEntity defaultEntity = getDefaultService().queryById(entityId);
      returnMap.put("result", defaultEntity);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      returnMap.put("errorMessage:", e.getMessage());
      return returnResultMessage(-999, "error", returnMap);
    }
    DefaultEntity defaultEntity;
    return returnSuccessMessage(returnMap);
  }
}
