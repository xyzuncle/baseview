package com.cenmobile.powerem.service;

import com.cenmobile.powerem.dao.businessgis.BusinessGISUtils;
import com.cenmobile.powerem.dao.entity.DefaultEntity;
import com.cenmobile.powerem.dao.impl.BaseDAO;
import com.cenmobile.powerem.exception.ServiceException;
import com.cenmobile.powerem.model.IQueryCondition;
import com.cenmobile.powerem.model.IResultSet;
import com.cenmobile.powerem.model.Paging;
import com.cenmobile.powerem.search.ISearchAction;
import com.cenmobile.powerem.search.extend.EntitySearchActionFactory;
import com.cenmobile.powerem.search.extend.event.SearchActionEventObject;
import com.cenmobile.powerem.search.extend.event.SearchActionEventPublisher;
import com.cenmobile.powerem.search.model.base.SearchModelValue;
import com.cenmobile.powerem.utils.ReflectionUtils;
import com.cenmobile.powerem.utils.SpringUtil;
import com.cenmobile.powerem.utils.ThreadLocalUtils;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BaseService<T extends DefaultEntity, S extends BaseDAO<T, String>>
{
  protected S defaultDAO;
  protected Class<T> entityClass;
  protected Log log = LogFactory.getLog(getClass());
  private static InheritableThreadLocal<Map> DefaultDAOThreadLocal = new InheritableThreadLocal();
  
  public BaseService() {
      if (this.getClass().getGenericSuperclass() instanceof ParameterizedType) {
              entityClass = ReflectionUtils.getSuperClassGenricTypes(getClass()).get(0);
              Class<S> clazz = ReflectionUtils.getSuperClassGenricTypes(getClass()).get(1);
              String name = clazz.getSimpleName();
              char c = name.charAt(0);
              if (c >= 'A' && c <= 'Z') {
                  char[] chars = name.toCharArray();
                  chars[0] = (char) (chars[0] + 32);
                  defaultDAO = (S) SpringUtil.getBean(String.valueOf(chars));
              } else {
                  defaultDAO = (S) SpringUtil.getBean(name);
              }

              Map<Class<T>, S> map = new HashMap<Class<T>, S>();
              map.put(entityClass, defaultDAO);
              DefaultDAOThreadLocal.set(map);
      } else {
          Map<Class<T>, S> map = (Map<Class<T>, S>) DefaultDAOThreadLocal.get();
          if (map != null) {
              for (Class<T> clazz : map.keySet()) {
                  entityClass = clazz;
                  defaultDAO = map.get(entityClass);
              }
          }
      }
  }
  
  protected void afterSave(T defaultEntity) {}
  
  public String save(T defaultEntity)
    throws ServiceException
  {
    ThreadLocalUtils.putServiceOperation(ThreadLocalUtils.SERVICE_OPERATION_ADD);
    if (defaultEntity == null) {
      throw new ServiceException("the entity has been null!");
    }
    String entityId = getDefaultDAO().save(defaultEntity);
    if (entityId != null) {
      if (EntitySearchActionFactory.isEntitySupportFullTextSearch(defaultEntity.getClass()))
      {
        ISearchAction searchAction = EntitySearchActionFactory.createSearchActionByEntity(defaultEntity.getClass());
        Map<String, SearchModelValue> searchModel = new HashMap();
        searchModel.put("id", SearchModelValue.getInstance(defaultEntity.getId(), true, false));
        String content = defaultEntity.toSearchString();
        searchModel.put("content", SearchModelValue.getInstance(content, false, true));
        searchModel.put("publishTime", SearchModelValue.getInstance(defaultEntity.getCreated(), true, true));
        
        SearchActionEventPublisher.sendSearchActionEvent(new SearchActionEventObject(searchAction, searchModel));
      }
    }
    afterSave(defaultEntity);
    return entityId;
  }
  
  public String saveBatch(Set<T> defaultEntitys)
    throws ServiceException
  {
    ThreadLocalUtils.putServiceOperation(ThreadLocalUtils.SERVICE_OPERATION_ADD);
    if ((defaultEntitys == null) || (defaultEntitys.size() == 0)) {
      throw new ServiceException("the entity has been null!");
    }
    String result = null;
    if (getDefaultDAO().exist(((DefaultEntity)defaultEntitys.iterator().next()).getId())) {
      result = getDefaultDAO().updateBatch(defaultEntitys);
    } else {
      result = getDefaultDAO().saveBatch(defaultEntitys);
    }
    for (T defaultEntity : defaultEntitys) {
      if (defaultEntity.getId() != null) {
        if (EntitySearchActionFactory.isEntitySupportFullTextSearch(defaultEntity.getClass()))
        {
          ISearchAction searchAction = EntitySearchActionFactory.createSearchActionByEntity(defaultEntity.getClass());
          Map<String, SearchModelValue> searchModel = new HashMap();
          searchModel.put("id", SearchModelValue.getInstance(defaultEntity.getId(), true, false));
          String content = defaultEntity.toSearchString();
          searchModel.put("content", SearchModelValue.getInstance(content, false, true));
          searchModel.put("publishTime", SearchModelValue.getInstance(defaultEntity.getCreated(), true, true));
          
          SearchActionEventPublisher.sendSearchActionEvent(new SearchActionEventObject(searchAction, searchModel));
        }
      }
    }
    return result;
  }
  
  public String saveBatch(List<T> defaultEntitys)
    throws ServiceException
  {
    ThreadLocalUtils.putServiceOperation(ThreadLocalUtils.SERVICE_OPERATION_ADD);
    Set<T> set = new HashSet();
    if ((defaultEntitys != null) && (defaultEntitys.size() > 0)) {
      set.addAll(defaultEntitys);
    }
    return saveBatch(set);
  }
  
  public String updateBatch(Set<T> defaultEntitys)
    throws ServiceException
  {
    ThreadLocalUtils.putServiceOperation(ThreadLocalUtils.SERVICE_OPERATION_UPDATE);
    if ((defaultEntitys == null) || (defaultEntitys.size() == 0)) {
      throw new ServiceException("the entity has been null!");
    }
    String result = getDefaultDAO().updateBatch(defaultEntitys);
    for (T defaultEntity : defaultEntitys) {
      if (defaultEntity.getId() != null) {
        if (EntitySearchActionFactory.isEntitySupportFullTextSearch(defaultEntity.getClass()))
        {
          ISearchAction searchAction = EntitySearchActionFactory.createSearchActionByEntity(defaultEntity.getClass());
          Map<String, SearchModelValue> searchModel = new HashMap();
          searchModel.put("id", SearchModelValue.getInstance(defaultEntity.getId(), true, false));
          String content = defaultEntity.toSearchString();
          searchModel.put("content", SearchModelValue.getInstance(content, false, true));
          searchModel.put("publishTime", SearchModelValue.getInstance(defaultEntity.getCreated(), true, true));
          
          SearchActionEventPublisher.sendSearchActionEvent(new SearchActionEventObject(searchAction, searchModel));
        }
      }
    }
    return result;
  }
  
  public String updateBatch(List<T> defaultEntitys)
    throws ServiceException
  {
    ThreadLocalUtils.putServiceOperation(ThreadLocalUtils.SERVICE_OPERATION_UPDATE);
    Set<T> set = new HashSet();
    if ((defaultEntitys != null) && (defaultEntitys.size() > 0)) {
      set.addAll(defaultEntitys);
    }
    return updateBatch(set);
  }
  
  public IResultSet query(IQueryCondition queryCondition)
    throws ServiceException
  {
    StringBuffer jpql = new StringBuffer("select entity from ");
    jpql.append(this.entityClass.getSimpleName()).append(" entity where 1=1");
    Map<String, Object> result = queryCondition.fetchQueryString();
    return query(queryCondition, jpql.append(result.get("jpql")), (List)result.get("values"));
  }
  
  public void flush()
  {
    getDefaultDAO().flush();
  }
  
  public List<Object[]> queryByJPSQL(StringBuffer jpql, List<Object> value)
    throws ServiceException
  {
    List<Object[]> resultSet = getDefaultDAO().findByJPSQL(jpql.toString(), value.toArray());
    return resultSet;
  }
  
  protected void afterDelete(String delId)
    throws ServiceException
  {}
  
  protected void preDelete(String delId)
    throws ServiceException
  {}
  
  public void delete(String id)
    throws ServiceException
  {
    ThreadLocalUtils.putServiceOperation(ThreadLocalUtils.SERVICE_OPERATION_DELETE);
    preDelete(id);
    getDefaultDAO().remove(id);
    getDefaultDAO();Class<T> daoEntityClass = (Class)BaseDAO.RelatedEntity.get();
    if (daoEntityClass == null) {
      daoEntityClass = this.entityClass;
    }
    if (daoEntityClass != null)
    {
      if (EntitySearchActionFactory.isEntitySupportFullTextSearch(daoEntityClass))
      {
        ISearchAction searchAction = EntitySearchActionFactory.createSearchActionByEntity(daoEntityClass);
        
        SearchActionEventPublisher.sendSearchActionEvent(new SearchActionEventObject(searchAction, id));
      }
      BusinessGISUtils.removeGISPlotInfo(getDefaultDAO(), daoEntityClass, id);
    }
    afterDelete(id);
  }
  
  protected void afterUpdate(T defaultEntity) {}
  
  public String update(T defaultEntity)
    throws ServiceException
  {
    ThreadLocalUtils.putServiceOperation(ThreadLocalUtils.SERVICE_OPERATION_UPDATE);
    if (defaultEntity == null) {
      throw new ServiceException("the entity has been null!");
    }
    getDefaultDAO().update(defaultEntity);
    if (defaultEntity.getId() != null) {
      if (EntitySearchActionFactory.isEntitySupportFullTextSearch(defaultEntity.getClass()))
      {
        ISearchAction searchAction = EntitySearchActionFactory.createSearchActionByEntity(defaultEntity.getClass());
        
        defaultEntity = getDefaultDAO().get(defaultEntity.getId());
        Map<String, SearchModelValue> searchModel = new HashMap();
        searchModel.put("id", SearchModelValue.getInstance(defaultEntity.getId(), true, false));
        String content = defaultEntity.toSearchString();
        searchModel.put("content", SearchModelValue.getInstance(content, false, true));
        searchModel.put("publishTime", SearchModelValue.getInstance(defaultEntity.getCreated(), true, true));
        
        SearchActionEventPublisher.sendSearchActionEvent(new SearchActionEventObject(searchAction, searchModel));
      }
    }
    afterUpdate(defaultEntity);
    return getId(defaultEntity);
  }
  
  public String saveorupdate(T defaultEntity)
    throws ServiceException
  {
    String id = defaultEntity.getId();
    if ((id != null) && 
      (exist(id)))
    {
      ThreadLocalUtils.putServiceOperation(ThreadLocalUtils.SERVICE_OPERATION_UPDATE);
      return update(defaultEntity);
    }
    ThreadLocalUtils.putServiceOperation(ThreadLocalUtils.SERVICE_OPERATION_ADD);
    return save(defaultEntity);
  }
  
  public String saveorignore(T defaultEntity)
    throws ServiceException
  {
    String id = getId(defaultEntity);
    if ((id != null) && 
      (queryById(id) != null)) {
      return id;
    }
    ThreadLocalUtils.putServiceOperation(ThreadLocalUtils.SERVICE_OPERATION_ADD);
    return save(defaultEntity);
  }
  
  public T queryById(String id)
    throws ServiceException
  {
    return getDefaultDAO().get(id);
  }
  
  public int executeByJPSQL(StringBuffer jpql, List<Object> values)
    throws ServiceException
  {
    ThreadLocalUtils.putServiceOperation(ThreadLocalUtils.SERVICE_OPERATION_UPDATE);
    int resultSet = getDefaultDAO().executeJPSQL(jpql.toString(), values.toArray());
    return resultSet;
  }
  
  @Deprecated
  protected S getDefaultDAO()
  {
    return this.defaultDAO;
  }
  
  protected IResultSet query(IQueryCondition queryCondition, StringBuffer jpql, List<Object> value) throws ServiceException {
      Paging paging = null;
      if (queryCondition.getPageSize() != 0) {
          paging = new Paging();
          paging.setCurrent(queryCondition.getPageIndex());
          paging.setSize(queryCondition.getPageSize());
      }
      IResultSet resultSet;
      if (paging != null) {
          // for 表格展示，带分页
          resultSet = getDefaultDAO().find(jpql.toString(), paging, value.toArray());
      } else {
          // for 报表查询，不需要分页
          resultSet = getDefaultDAO().findWithoutPagging(jpql.toString(), value.toArray());
      }
      return resultSet;
  }
  private String getId(T entity)
  {
    if (entity == null) {
      return null;
    }
    return entity.getId();
  }
  
  public boolean exist(String id)
  {
    return getDefaultDAO().exist(id);
  }
  
  private static Map getParamTypeInfo()
  {
    return (Map)DefaultDAOThreadLocal.get();
  }
  
  private static void setParamTypeInfo(Map typeInfo)
  {
    DefaultDAOThreadLocal.set(typeInfo);
  }
}
