package com.cenmobile.powerem.base;

import com.cenmobile.powerem.dao.entity.attachement.EMAttachment;
import com.cenmobile.powerem.dto.ReturnMessageDTO;
import com.cenmobile.powerem.utils.ContextFilePathUtil;
import com.cenmobile.powerem.utils.JSONUtil;
import com.cenmobile.powerem.utils.RestRequestClient;
import com.cenmobile.powerem.utils.RestUtil;
import com.cenmobile.powerem.utils.StringUtil;
import com.opensymphony.xwork2.ActionSupport;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;

public class BaseAction extends ActionSupport
{

  public void aaaaaa(){};

  public void getImageByAttachment()
  {
    String id = ServletActionContext.getRequest().getParameter("attchmentId");
    getImageByAttachment(id);
  }

  public void getFileByAttachment()
  {
    String id = ServletActionContext.getRequest().getParameter("attchmentId");
    getFileByAttachment(id);
  }

  public void getDWGByAttachement() {
    String id = ServletActionContext.getRequest().getParameter("attchmentId");
    ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename=" + id + ".dwg");
    getFileByAttachment(id);
  }

  public void getImageThumbnailByAttachment()
    throws IOException
  {
    String id = ServletActionContext.getRequest().getParameter("attchmentId");
    String width_ = ServletActionContext.getRequest().getParameter("width");
    String height_ = ServletActionContext.getRequest().getParameter("height");
    String rate = ServletActionContext.getRequest().getParameter("rate");
    byte[] byteArray = getByteArrayByAttachmentId(id);
    String filePath = ContextFilePathUtil.getAttachmentFilePath("ewarning_image") + "/" + id;
    if (new File(filePath + "_").exists()) {
      getWriteImage(FileUtils.readFileToByteArray(new File(filePath + "_")));
      return;
    }
    Image src = ImageIO.read(new File(filePath));
    int wideth = src.getWidth(null);
    int height = src.getHeight(null);
    int newWidth = wideth; int newHeight = height;
    if ((width_ != null) && (width_.length() != 0)) {
      rate = String.valueOf(Integer.parseInt(width_) * 1.0D / wideth);
    }
    if ((height_ != null) && (height_.length() != 0)) {
      if (!StringUtil.isBlank(rate)) rate = null; else
        rate = String.valueOf(Integer.parseInt(height_) * 0.1D / height);
    }
    if ((rate != null) && (rate.length() != 0)) {
      float rateI = Float.parseFloat(rate);
      newWidth = Math.round(newWidth * rateI);
      newHeight = Math.round(newHeight * rateI);
    }
    BufferedImage tag = new BufferedImage(newWidth, newHeight, 1);
    tag.getGraphics().drawImage(src, 0, 0, newWidth, newHeight, null);
    FileOutputStream out = new FileOutputStream(filePath + "_");
    JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
    encoder.encode(tag);
    out.close();
    getWriteImage(FileUtils.readFileToByteArray(new File(filePath + "_")));
  }

  public void getImageByBussinessTypeAndBussinessId()
  {
    String businessType = ServletActionContext.getRequest().getParameter("businessType");
    String businessId = ServletActionContext.getRequest().getParameter("businessId");
    String attachmentType = ServletActionContext.getRequest().getParameter("attachmentType");
    RestUtil restUtil = new RestUtil(EMAttachment.class);
    Map map = new HashMap();
    map.put("entity.businessId", businessId);
    map.put("entity.businessType", businessType);
    if ((attachmentType != null) && (attachmentType.length() > 0)) {
      map.put("entity.attachmentType", attachmentType);
    }
    List<EMAttachment> list = restUtil.getList(map);
    if ((list != null) && (list.size() != 0))
      for (EMAttachment emAttachment : list)
        if (emAttachment != null) {
          getImageByAttachment(emAttachment.getId());
          break;
        }
  }

  public void cacheAttachmentToFile()
  {
    String id = ServletActionContext.getRequest().getParameter("attchmentId");
    if ((id != null) && (id.length() != 0)) {
      getByteArrayByAttachmentId(id);
    }
    String businessType = ServletActionContext.getRequest().getParameter("businessType");
    String businessId = ServletActionContext.getRequest().getParameter("businessId");
    String attachmentType = ServletActionContext.getRequest().getParameter("attachmentType");
    RestUtil restUtil = new RestUtil(EMAttachment.class);
    Map map = new HashMap();
    map.put("entity.businessId", businessId);
    map.put("entity.businessType", businessType);
    if ((attachmentType != null) && (attachmentType.length() > 0)) {
      map.put("entity.attachmentType", attachmentType);
    }
    List<EMAttachment> list = restUtil.getList(map);
    if ((list != null) && (list.size() != 0))
      for (EMAttachment emAttachment : list)
        if (emAttachment != null)
          getByteArrayByAttachmentId(emAttachment.getId());
  }

  private void getImageByAttachment(String entityId)
  {
    if ((entityId == null) || (entityId.length() == 0)) {
      return;
    }
    getWriteImage(getByteArrayByAttachmentId(entityId));
  }

  private void getFileByAttachment(String entityId)
  {
    if ((entityId == null) || (entityId.length() == 0)) {
      return;
    }
    getWriteFile(getByteArrayByAttachmentId(entityId));
  }

  private byte[] getByteArrayByAttachmentId(String entityId)
  {
    String filePath = ContextFilePathUtil.getAttachmentFilePath("ewarning_image") + "/" + entityId;
    File attachmentFile = new File(filePath);
    byte[] att = (byte[])null;
    try {
      RestRequestClient.getInstance().getFileInputStreamByAttachmentId(entityId);

      if (attachmentFile.exists()) {
        att = FileUtils.readFileToByteArray(attachmentFile);
      }
      if (att == null) {
        String response = RestRequestClient.getInstance().getRestJsonContentWithoutParameter(EMAttachment.class.getSimpleName() + "/" + entityId + "/attachments");
        ReturnMessageDTO jsonReturn = RestRequestClient.getInstance().getReturnMssage(response);
        att = (byte[])JSONUtil.convertJsonObjectToBean(jsonReturn.getInfo().get("result"), byte[].class);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return att;
  }

  public void getWriteImage(byte[] imagebinary)
  {
    if ((imagebinary == null) || (imagebinary.length == 0))
      return;
    try
    {
      ServletActionContext.getResponse().setContentType("image/jpeg");

      ServletActionContext.getResponse().setHeader("Cache-Control", "public");
      OutputStream outp = ServletActionContext.getResponse().getOutputStream();
      try {
        outp.write(imagebinary);
        outp.flush();
        ServletActionContext.getResponse().flushBuffer();
        outp.close();
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        outp.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void getWriteFile(byte[] imagebinary)
  {
    if ((imagebinary == null) || (imagebinary.length == 0))
      return;
    try
    {
      ServletActionContext.getResponse().setContentType("application/octet-stream");
      OutputStream outp = ServletActionContext.getResponse().getOutputStream();
      try {
        outp.write(imagebinary);
        outp.flush();
        ServletActionContext.getResponse().flushBuffer();
        outp.close();
      } catch (Exception e) {
        System.out.println("Error!");
        e.printStackTrace();
      } finally {
        outp.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}