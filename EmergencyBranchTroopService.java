package com.cenmobile.powerem.service.eprepare;

import com.cenmobile.powerem.dao.entity.DefaultEntity;
import com.cenmobile.powerem.dao.impl.BaseDAO;
import com.cenmobile.powerem.dao.module.eprepare.EmergencyBranchTroopDAO;
import com.cenmobile.powerem.exception.ServiceException;
import com.cenmobile.powerem.model.IQueryCondition;
import com.cenmobile.powerem.model.IResultSet;
import com.cenmobile.powerem.service.BaseService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmergencyBranchTroopService extends BaseService
{

  @Autowired
  private EmergencyBranchTroopDAO emergencyBranchTroopDAO;

  protected BaseDAO<DefaultEntity, String> getDefaultDAO()
  {
    return (BaseDAO)emergencyBranchTroopDAO;
  }

  public IResultSet query(IQueryCondition queryCondition) throws ServiceException
  {
    List value = new ArrayList();
    StringBuffer jpql = new StringBuffer("select entity from EmergencyBranchTroop entity where 1=1");
    if ((queryCondition != null) && (queryCondition.getAllQueryConditions().size() > 0)) {
      if (queryCondition.getQueryCondition("entity.areaCode") != null) {
        jpql.append(" AND entity.areaCode like :areaCode");
        value.add("%" + queryCondition.getQueryCondition("entity.areaCode") + "%");
      }
      if (queryCondition.getQueryCondition("entity.teamName") != null) {
        jpql.append(" AND entity.teamName like :teamName");
        value.add("%" + queryCondition.getQueryCondition("entity.teamName") + "%");
      }
      if (queryCondition.getQueryCondition("entity.superiordep") != null) {
        jpql.append(" AND entity.superiordep = :superiordep");
        value.add(queryCondition.getQueryCondition("entity.superiordep"));
      }
      if (queryCondition.getQueryCondition("entity.teamType") != null) {
        jpql.append(" AND entity.teamType = :teamType");
        value.add(queryCondition.getQueryCondition("entity.teamType"));
      }
      if (queryCondition.getQueryCondition("entity.dutyPerson") != null) {
        jpql.append(" AND entity.dutyPerson = :leader");
        value.add(queryCondition.getQueryCondition("entity.dutyPerson"));
      }
      if (queryCondition.getQueryCondition("entity.leaderPhone") != null) {
        jpql.append(" AND entity.leaderPhone = :leaderPhone");
        value.add(queryCondition.getQueryCondition("entity.leaderPhone"));
      }
      if (queryCondition.getQueryCondition("entity.troopId") != null) {
        jpql.append(" AND entity.emergencyTroop.id = :id");
        value.add(queryCondition.getQueryCondition("entity.troopId"));
      }
    }

    return super.query(queryCondition, jpql, value);
  }
}