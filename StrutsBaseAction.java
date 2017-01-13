package com.cenmobile.powerem.base;

import com.cenmobile.powerem.dao.entity.DefaultEntity;
import com.cenmobile.powerem.dao.entity.attachement.EMAttachment;
import com.cenmobile.powerem.dto.ReturnMessageDTO;
import com.cenmobile.powerem.dto.fusionchart.ChartSet;
import com.cenmobile.powerem.exception.ActionException;
import com.cenmobile.powerem.exception.JSONException;
import com.cenmobile.powerem.model.IQueryCondition;
import com.cenmobile.powerem.model.IResultSet;
import com.cenmobile.powerem.utils.ContextFilePathUtil;
import com.cenmobile.powerem.utils.DateUtil;
import com.cenmobile.powerem.utils.JSONUtil;
import com.cenmobile.powerem.utils.LogUtil;
import com.cenmobile.powerem.utils.ReflectionUtils;
import com.cenmobile.powerem.utils.RestRequestClient;
import com.cenmobile.powerem.utils.UUIDGenerator;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import java.awt.Color;
import java.awt.Font;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.ui.TextAnchor;

public abstract class StrutsBaseAction<T> extends ActionSupport
  implements Preparable
{
  protected Class<T> entityClass;
  protected static final String splitChar = "@###@";
  public static final String[] reportColor = { "#FF0066", "#660099", "#006400", "#008080", "#87CEFA", "#9999FF", "#009900", "#CC9966", "#AFEEEE", "#000033", "#99FFFF", "#FFCC33", "#66FF33", "#CCFFFF" };
  private static final long serialVersionUID = -7514251005272463731L;
  public static final String CANCEL = "cancel";
  protected final transient LogUtil log = new LogUtil(getClass().getName());
  protected String cancel;
  protected String from;
  protected String delete;
  protected String save;
  protected JRDataSource reportDataSource;
  protected Map<String, Object> reportParams = new HashMap();

  public StrutsBaseAction()
  {
    this.entityClass = ((Class)ReflectionUtils.getSuperClassGenricTypes(getClass()).get(0));
  }

  public String cancel()
  {
    return "cancel";
  }

  public void setFrom(String from)
  {
    this.from = from;
  }

  public void setDelete(String delete) {
    this.delete = delete;
  }

  public void setSave(String save) {
    this.save = save;
  }

  protected HttpServletResponse getResponse() {
    return ServletActionContext.getResponse();
  }

  protected HttpServletRequest getRequest() {
    return ServletActionContext.getRequest();
  }

  public void getWrite(String outString) {
    try {
      getResponse().setContentType("text/html");

      outString = URLDecoder.decode(outString, "UTF-8");
      outString = outString.replaceAll("@###@", "'");

      PrintWriter pw = new PrintWriter(new OutputStreamWriter(getResponse().getOutputStream(), "utf-8"));
      pw.write(outString);
      pw.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
/**
 * 输出流 附件相关
 * @param binary
 * @param downloadfilename
 */
  public void getWrite(byte[] binary, String downloadfilename) {
    try {
      getResponse().setContentType("application/octet-stream");
      String filedisplay = URLEncoder.encode(downloadfilename, "UTF-8");
      filedisplay = StringUtils.replace(filedisplay, "+", "%20");

      if (getRequest().getHeader("User-Agent").toLowerCase().indexOf("firefox") > -1)
        getResponse().setHeader("Content-Disposition", "attachment;filename=\"" + filedisplay + "\"");
      else {
        getResponse().setHeader("Content-Disposition", "attachment;filename=" + filedisplay);
      }

      OutputStream outp = null;
      try {
        outp = getResponse().getOutputStream();
        outp.write(binary);
        outp.flush();
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
//获取上下文
  protected String getContextPath() {
    return getRequest().getContextPath();
  }
/**
 *  封装多条件查询
 * @param queryCondition
 * @return
 * @throws JSONException
 */
  public IResultSet query(IQueryCondition queryCondition) throws JSONException {
    if (queryCondition != null) {
      String page = getRequest().getParameter("page");
      if ((page != null) && (!"".equals(page)))
        queryCondition.setPageIndex(Integer.valueOf(page).intValue());
      String rows = getRequest().getParameter("rows");
      if ((rows != null) && (!"".equals(rows)))
        queryCondition.setPageSize(Integer.valueOf(rows).intValue());
    }
    String conditions = JSONUtil.serialize(queryCondition);
    //调用相关的**Resource
    String response = RestRequestClient.getInstance().getRestJsonContentWithHeader(this.entityClass.getSimpleName(), "conditions", conditions);
    //把查询得到的结果封装到 IResultSet 对象中
    IResultSet resultSet = RestRequestClient.getInstance().getResultSetByMessage(response);
    return resultSet;
  }

  /**
   * 修改或保存实体
   * @param entity
   * @return
   * @throws JSONException
   */
  public String saveOrUpdateEntity(DefaultEntity entity) throws JSONException {
    getResponse().setContentType("text/html; charset=UTF-8");
    String response = null;
    String entityId = null;
    //如果得到ID为空，则为新增
    try {
      if ((entity != null) && (entity.getId() != null) && (!"".equals(entity.getId()))) {
        String entityJson = JSONUtil.serialize(entity);
        response = RestRequestClient.getInstance().putRestRequestWithParamter(this.entityClass.getSimpleName() + "/" + entity.getId(), entityJson);
        entityId = entity.getId();
      } else {
    	  //否则修改实体
        String jsonUser = JSONUtil.serialize(entity);
        response = RestRequestClient.getInstance().postRestRequestWithParamter(this.entityClass.getSimpleName(), jsonUser);
        entityId = RestRequestClient.getInstance().getReturnEntityIdByMessage(response);
      }
    } catch (Exception e) {
      e.printStackTrace();
      getWrite(returnResultMessage(-999, "error", null));
    }
    getWrite(response);
    return entityId;
  }
//得到附件
  public EMAttachment getEmAttachment(EMAttachment attachment, DefaultEntity entity)
    throws ActionException
  {
    try
    {
      if ((attachment != null) && (attachment.getFile() != null) && (attachment.getFile().length > 0) && (attachment.getFile()[0] != null))
      {
        String indexFilePath = getWebContentAttachmentPath() + "\\" + UUIDGenerator.generateUUID();
        attachment.setAttachment(FileUtils.readFileToByteArray(attachment.getFile()[0]));
        attachment.setAttachmentName(attachment.getFileFileName()[0]);
        attachment.setAttachmentContentType(attachment.getFileContentType()[0]);

        FileUtils.copyFile(attachment.getFile()[0], new File(indexFilePath));
        attachment.setAttachmentLocalIndex(indexFilePath);
        if ((attachment.getId() == null) || ("".equals(attachment.getId())))
        {
          attachment.setId(UUIDGenerator.generateUUID());
        }
        attachment.setBusinessId(entity.getId());
        attachment.setBusinessType(entity.getClass().getSimpleName());

        attachment.setFile(null);
        attachment.setFileContentType(null);
        attachment.setFileFileName(null);
        return attachment;
      }
      return null;
    } catch (Exception e) {
    	 throw new ActionException(e);
    }
   
  }
//得到附件列表
  public List<EMAttachment> getEmAttachmentList(EMAttachment attach, DefaultEntity entity)
    throws ActionException
  {
    List attachments = new ArrayList();
    try {
      if ((attach != null) && (attach.getFile() != null) && (attach.getFile().length > 0)) {
        for (int i = 0; i < attach.getFile().length; i++) {
          EMAttachment attachment = new EMAttachment();

          String indexFilePath = getWebContentAttachmentPath() + "\\" + UUIDGenerator.generateUUID();
          attachment.setAttachment(FileUtils.readFileToByteArray(attach.getFile()[i]));
          attachment.setAttachmentName(attach.getFileFileName()[i]);
          attachment.setAttachmentContentType(attach.getFileContentType()[i]);

          FileUtils.copyFile(attach.getFile()[i], new File(indexFilePath));
          attachment.setAttachmentLocalIndex(indexFilePath);
          if ((attach.getId() == null) || ("".equals(attach.getId())))
          {
            attachment.setId(UUIDGenerator.generateUUID());
          }
          attachment.setBusinessId(entity.getId());
          attachment.setBusinessType(entity.getClass().getSimpleName());

          attachment.setFile(null);
          attachment.setFileContentType(null);
          attachment.setFileFileName(null);
          attachments.add(attachment);
        }
      }

      return attachments; }
    catch (Exception e) {
    	  throw new ActionException(e);
    }
    
  }
//下载附件
  public void downloadAttachmentFile(String attachmentId)
    throws ActionException
  {
    String response = RestRequestClient.getInstance().getRestJsonContentWithoutParameter(EMAttachment.class.getSimpleName() + "/" + attachmentId);
    ReturnMessageDTO jsonReturn = RestRequestClient.getInstance().getReturnMssage(response);
    EMAttachment emAttachment = (EMAttachment)JSONUtil.convertJsonObjectToBean(jsonReturn.getInfo().get("result"), EMAttachment.class);
    String fileIndex = emAttachment.getAttachmentLocalIndex();
    File localFile = new File(fileIndex);
    if (!localFile.exists()) {
      response = RestRequestClient.getInstance().getRestJsonContentWithoutParameter(EMAttachment.class.getSimpleName() + "/" + attachmentId + "/attachments");
      jsonReturn = RestRequestClient.getInstance().getReturnMssage(response);
      byte[] SummaryFileByte = (byte[])JSONUtil.convertJsonObjectToBean(jsonReturn.getInfo().get("result"), byte[].class);
      try {
        FileUtils.writeByteArrayToFile(localFile, SummaryFileByte);
      } catch (IOException e) {
        e.printStackTrace();
      }
      byte[] arrayOfByte1 = SummaryFileByte;
    }
    byte[] fileByte;
    try {
      fileByte = FileUtils.readFileToByteArray(localFile);
    }
    catch (IOException e)
    {
    
      throw new JSONException(e);
    }
     
    getWrite(fileByte, emAttachment.getAttachmentName());
  }
 /**
  * 删除实体
  */
  public void deleteEntity() {
    String response = "";
    String entityids = getRequest().getParameter("ids");
    entityids = entityids.replaceAll("\"", "");

    String[] ids = entityids.split(",");
    try {
      for (String id : ids)
        response = RestRequestClient.getInstance().deleteRestRequestWithParamter(this.entityClass.getSimpleName() + "/" + id);
    }
    catch (Exception e) {
      e.printStackTrace();
      getWrite(returnResultMessage(-999, "error", null));
    }
    getWrite(response);
  }
/**
 * 根据实体得到ID
 * @throws JSONException
 */
  public void getEntityById() throws JSONException {
    String entityId = getRequest().getParameter("entity.id");
    if(entityId==null){
    	entityId = getRequest().getParameter("id");
    }
    String response = RestRequestClient.getInstance().getRestJsonContentWithoutParameter(this.entityClass.getSimpleName() + "/" + entityId);
    ReturnMessageDTO jsonReturn = RestRequestClient.getInstance().getReturnMssage(response);
    DefaultEntity defaultEntity = (DefaultEntity)JSONUtil.convertJsonObjectToBean(jsonReturn.getInfo().get("result"), this.entityClass);
    getWrite(JSONUtil.serialize(defaultEntity));
  }

  public DefaultEntity getEntityInstanceById() throws JSONException {
    String entityId = getRequest().getParameter("entity.id");
    if (StringUtils.isNotBlank(entityId)) {
      String response = RestRequestClient.getInstance().getRestJsonContentWithoutParameter(this.entityClass.getSimpleName() + "/" + entityId);
      ReturnMessageDTO jsonReturn = RestRequestClient.getInstance().getReturnMssage(response);
      DefaultEntity defaultEntity = (DefaultEntity)JSONUtil.convertJsonObjectToBean(jsonReturn.getInfo().get("result"), this.entityClass);
      return defaultEntity;
    }
    return null;
  }

  public String getXmlByObj(Object object)
  {
    StringBuffer sb = new StringBuffer();
    Field[] fields = object.getClass().getDeclaredFields();
    for (Field field : fields) {
      String[] value = new String[2];
      value[0] = field.getName();
      value[1] = "";
      String propertyName = value[0].substring(0, 1).toUpperCase() + value[0].substring(1);
      Method methods = null;
      try {
        if (field.getType().toString().equals("boolean"))
          methods = object.getClass().getMethod("is" + propertyName, new Class[0]);
        else
          methods = object.getClass().getMethod("get" + propertyName, new Class[0]);
      }
      catch (Exception e) {
        this.log.debug(e.getMessage());
      }
      if (methods == null)
        continue;
      try {
        Object obj = methods.invoke(object, new Object[0]);
        if (obj != null)
          value[1] = obj.toString();
        sb.append("<").append(value[0]).append(">");
        sb.append("<![CDATA[");
        sb.append(value[1]);
        sb.append("]]>");
        sb.append("</").append(value[0]).append(">");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return sb.toString();
  }

  public String serialObject(Object object) {
    StringBuffer sb = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    sb.append("<ROOT>");
    sb.append("<className>").append(object.getClass().getName()).append("</className>");
    sb.append(getXmlByObj(object));
    sb.append("</ROOT>");
    return sb.toString();
  }

  public Object deSerial(String xml)
  {
    Object obj = null;
    try {
      if (xml != null) {
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(new ByteArrayInputStream(xml.getBytes()));
        Element rootElement = document.getRootElement();
        List<Element> childList = rootElement.getChildren();
        for (Element ele : childList) {
          if (ele.getName().equals("className")) {
            obj = Class.forName(ele.getText()).newInstance();
          }
          if (obj != null) {
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
              if (!ele.getName().equals(field.getName())) continue;
              try {
                String propertyName = field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
                Method methods = obj.getClass().getMethod("set" + propertyName, new Class[] { field.getType() });
                if (methods != null)
                  if (field.getType().getName().equals("java.util.Date"))
                    methods.invoke(obj, new Object[] { DateUtil.convertStringToDate("yyyy-MM-dd HH:mm:ss", ele.getText()) });
                  else
                    methods.invoke(obj, new Object[] { ele.getText() });
              }
              catch (Exception e)
              {
                this.log.debug(e.getMessage());
              }
            }
          }
        }
      }
    }
    catch (Exception e) {
      this.log.debug(e.getMessage());
    }
    return obj;
  }

  public String getUploadFilePath() {
    String uploadFilePath = ServletActionContext.getServletContext().getRealPath("upload");
    File uploadDir = new File(uploadFilePath);
    if (!uploadDir.exists())
      uploadDir.mkdirs();
    return uploadFilePath;
  }

  public void prepare()
    throws Exception
  {
  }

  public String returnSuccessMessage()
  {
    return returnSuccessMessage(null);
  }

  public String returnSuccessMessage(Map<String, Object> info) {
    return returnResultMessage(0, "OK", info);
  }

  public String returnResultMessage(int resultCode, String resultMessage, Map<String, Object> info) {
    ReturnMessageDTO returnMsg = new ReturnMessageDTO();
    returnMsg.setCode(resultCode);
    returnMsg.setInfo(info);
    returnMsg.setMessage(resultMessage);
    try {
      return JSONUtil.serialize(returnMsg); } catch (JSONException e) {
    }
    return null;
  }

  public void generateReport(String reportXMLFile) throws ActionException
  {
    try {
      String jasperFileURL = Thread.currentThread().getContextClassLoader().getResource("report").getPath();
      String jasperFilePath = jasperFileURL + reportXMLFile;
      String jasperCompliedFilePath = jasperFileURL + reportXMLFile + ".report";
      File jasperFile = new File(jasperFilePath);
      File jasperCompliedFile = new File(jasperCompliedFilePath);
      if (jasperFile.exists()) {
        if ((jasperCompliedFile.exists()) && (jasperFile.lastModified() < jasperCompliedFile.lastModified())) {
          return;
        }
        File parent = new File(jasperFilePath).getParentFile();
        JasperCompileManager.compileReportToFile(jasperFilePath, new File(parent, reportXMLFile + ".report").getAbsolutePath());
      }
      else {
        throw new ActionException("the report file is not existing ! file path is : " + jasperFilePath);
      }
    } catch (JRException e) {
      throw new ActionException(e);
    }
  }

  public Map<String, Object> getReportParams()
  {
    return this.reportParams;
  }

  public void setReportParams(Map<String, Object> reportParams) {
    this.reportParams = reportParams;
  }

  public JRDataSource getReportDataSource() {
    return this.reportDataSource;
  }

  public void setReportDataSource(List reportDataSet)
  {
    Map dataMap = new HashMap();
    List dataList = new ArrayList();
    dataMap.put("dataset", reportDataSet);
    dataList.add(dataMap);
    JRMapCollectionDataSource dataSource = new JRMapCollectionDataSource(dataList);
    this.reportDataSource = dataSource;
  }

  protected String getWebContentImagePath() {
    return ContextFilePathUtil.getAttachmentFilePath("images");
  }

  protected String getWebContentAttachmentPath() {
    return ContextFilePathUtil.getAttachmentFilePath("powerem_attach");
  }

  public String genBarChart3D(Dataset dataset, String chartTitle, String xTitle, String yTitle, boolean needBottorm)
  {
    if (dataset == null) {
      return null;
    }
    String chartImageName = UUIDGenerator.generateUUID() + ".jpg";
    String chartImagePath = getWebContentImagePath() + "/" + chartImageName;

    File chartImageFile = new File(chartImagePath);
    if (!chartImageFile.exists()) {
      System.out.println("chart image path is : " + chartImagePath);

      Font xfont = new Font("黑体", 1, 24);
      Font yfont = new Font("黑体", 1, 24);
      Font kfont = new Font("黑体", 1, 30);
      Font titleFont = new Font("隶书", 1, 30);

      JFreeChart chart = ChartFactory.createBarChart3D(chartTitle, xTitle, yTitle, (CategoryDataset)dataset, PlotOrientation.VERTICAL, needBottorm, false, false);
      chart.setTitle(new TextTitle(chartTitle, titleFont));
      CategoryPlot plot = chart.getCategoryPlot();
      plot.setBackgroundPaint(Color.decode("#ECE9D8"));
      plot.setDomainGridlinePaint(Color.decode("#799AE1"));
      plot.setRangeGridlinePaint(Color.decode("#799AE1"));
      plot.setBackgroundAlpha(0.9F);
      if (chart.getLegend() != null) {
        chart.getLegend().setItemFont(kfont);
      }

      CategoryAxis domainAxis = plot.getDomainAxis();
      domainAxis.setLabelFont(xfont);
      domainAxis.setTickLabelFont(xfont);
      domainAxis.setLabelPaint(Color.BLUE);
      domainAxis.setTickLabelPaint(Color.BLUE);
      domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

      ValueAxis rangeAxis = plot.getRangeAxis();
      rangeAxis.setLabelFont(yfont);
      rangeAxis.setLabelPaint(Color.BLUE);
      rangeAxis.setTickLabelFont(yfont);
      rangeAxis.setTickLabelPaint(Color.BLUE);

      BarRenderer3D renderer = new BarRenderer3D();
      renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
      renderer.setBaseItemLabelsVisible(true);
      renderer.setBaseFillPaint(Color.decode("#799AE1"));
      renderer.setBaseItemLabelFont(xfont);
      renderer.setMaximumBarWidth(100.0D);
      renderer.setBasePositiveItemLabelPosition(
        new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));
      renderer.setItemLabelAnchorOffset(10.0D);
      plot.setRenderer(renderer);

      plot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
      plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
      plot.setWeight(300);
      try
      {
        OutputStream os = new FileOutputStream(chartImagePath);
        try {
          ChartUtilities.writeChartAsJPEG(os, chart, 1280, 800);
        } catch (IOException e) {
          e.printStackTrace();
        }
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    }
    return chartImagePath;
  }

  protected Dataset getDataset(Map<String, Number> dataSet)
  {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    for (Iterator iter = dataSet.keySet().iterator(); iter.hasNext(); ) {
      String name = (String)iter.next();
      Number number = (Number)dataSet.get(name);
      dataset.addValue(number, name, name);
    }
    return dataset;
  }

  protected List<ChartSet> getFusionChartDateByDataSet(Map<String, Number> dataSet)
  {
    List chartSets = new ArrayList();
    for (Iterator iter = dataSet.keySet().iterator(); iter.hasNext(); ) {
      String name = (String)iter.next();
      Number number = (Number)dataSet.get(name);
      chartSets.add(new ChartSet(name, number.toString()));
    }
    return chartSets;
  }

  protected Dataset getDatasetByMap(Map<Object[], Number> dataSet)
  {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    for (Iterator iter = dataSet.keySet().iterator(); iter.hasNext(); ) {
      Object[] name = (Object[])iter.next();
      Number number = (Number)dataSet.get(name);
      dataset.addValue(number, String.valueOf(name[1]), String.valueOf(name[0]));
    }
    return dataset;
  }
}