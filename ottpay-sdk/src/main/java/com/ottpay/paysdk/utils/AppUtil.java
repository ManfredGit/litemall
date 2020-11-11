package com.ottpay.paysdk.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ottpay.paysdk.exceptions.ApiException;
import com.ottpay.paysdk.models.ApiErrorResponse;
import com.ottpay.paysdk.models.ApiInvalidResponse;
import com.ottpay.paysdk.models.ApiSuccessResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.tika.Tika;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.*;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class AppUtil {
    private static final Logger logger = LoggerFactory.getLogger(AppUtil.class);

    static String[] availableFileType = {"image/jpeg", "image/png", "image/gif", "image/x-ms-bmp"};

    public static String formatDate(Date date, String format) {
        if (date != null) {
            if (format != null) {
                SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
                return dateFormatter.format(date);
            } else {
                return date.toString();
            }
        } else {
            return "";
        }
    }

    public static Date parseDate(String dateInString, String format) {
        if(isEmpty(format))
            format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        if(isEmpty(dateInString)) return null;
        try {
            Date date = formatter.parse(dateInString);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date getStartOfDate(Date date) {
        return new DateTime(date).withTimeAtStartOfDay().toDate();
    }

    public static Date getEndOfDate(Date date) {
        return new DateTime(date).plusDays(1).withTimeAtStartOfDay().toDate();
    }

    private static final BigInteger TWO_64 = BigInteger.ONE.shiftLeft(64);

    public static String setNullIfEmpty(String value) {
        return isEmpty(value) ? null : value;
    }

    public static Boolean isEmpty(String string) {
        return string == null || "".equals(string.trim());
    }

    public static Boolean isMultipartFileEmpty(MultipartFile file) {
        return file == null || file.isEmpty();
    }

    public static Boolean isValidPassword(String password) {
        return password != null && !"".equals(password.trim());
    }

    public static String capitalize(String value) {
        return value == null ? "" : value.toUpperCase();
    }

    public static String toCurrencyString(BigDecimal value) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(value);
    }

    public static String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }

    public static BigDecimal numberOrZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    public static Integer numberOrZero(Integer value) {
        return value == null ? 0 : value;
    }

    public static String longToString(long l) {
        BigInteger b = BigInteger.valueOf(l);
        if (b.signum() < 0) {
            b = b.add(TWO_64);
        }
        return b.toString();
    }

    public static BigDecimal getBigDecimal(String value) {
        return value == null ? BigDecimal.ZERO : new BigDecimal(value);
    }

    public static String getRandomString(int length) {
        String CharCandidit = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < length) {
            int index = (int) (rnd.nextFloat() * CharCandidit.length());
            salt.append(CharCandidit.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }

    public static String getFileNameTimeStamp() {
        Date now = new Date();
        return String.format("%02d%02d%02d",now.getHours(), now.getMinutes(), now.getSeconds());
    }

    public static String getTempFileName() {
        return formatDate(new Date(), "yyyy-MM-dd-HH-mm-ss-SSS");
    }

    public static String getRandomTimeStamp() {
        Date now = new Date();
        return String.format("%02d%02d%02d", now.getHours(), now.getMinutes(), now.getSeconds());
    }

    public static String filterPhone(String phone) {
        if(phone == null) return null;
        return phone.replaceAll("\\D", "");
    }

    public static ResponseEntity<ApiSuccessResponse> successResponse(Object result) {
        ApiSuccessResponse successResponse = new ApiSuccessResponse(result);
        logger.debug("API success");
        return ResponseEntity.ok(successResponse);
    }

    public static ResponseEntity<ApiSuccessResponse> successResponse() {
        return ResponseEntity.ok(new ApiSuccessResponse(null));
    }

    public static ResponseEntity<ApiErrorResponse> errorResponse(Integer code, Object result) {
        ApiErrorResponse errorResponse = new ApiErrorResponse(result);
        ObjectMapper mapper = new ObjectMapper();
        try {
            logger.debug("API " + mapper.writeValueAsString(errorResponse.getResult()));
        } catch (JsonProcessingException e) {
            logger.debug(String.format("API error. code: %d", code));
        }
        return ResponseEntity.status(code).body(errorResponse);
    }

    public static ResponseEntity<ApiInvalidResponse> invalidResponse(Object result) {
        ApiInvalidResponse invalidResponse = new ApiInvalidResponse(result);
        logger.debug(String.format("API invalid data. data: %s", invalidResponse.getResult()));
        return ResponseEntity.status(400).body(invalidResponse);
    }

    public static ResponseEntity<ApiInvalidResponse> unauthorizedResponse(Object result) {
        ApiInvalidResponse invalidResponse = new ApiInvalidResponse(result);
        logger.debug(String.format("API invalid request data: %s", invalidResponse.getResult()));
        return ResponseEntity.status(401).body(invalidResponse);
    }
    public static String md5(String value) {
        return DigestUtils.md5Hex(value);
    }

    public static <T> T getFirstResult(List<T> list) {
        if(list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public static String encodePassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

    public static Boolean isValidFileType(MultipartFile file) {
        if(file == null || file.isEmpty()) { return false; }

        Boolean result = false;
        Tika tika = new Tika();
        try {
            String mediaType = tika.detect(file.getInputStream());
            if(Arrays.asList(availableFileType).contains(mediaType)) {
                result = true;
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            logger.debug(e.getMessage());
        }
        return result;
    }

    public static void copyProperty(Object source, Object dest) {
        try{
            //获取属性
            BeanInfo sourceBean = Introspector.getBeanInfo(source.getClass(), Object.class);
            PropertyDescriptor[] sourceProperty = sourceBean.getPropertyDescriptors();

            BeanInfo destBean = Introspector.getBeanInfo(dest.getClass(), Object.class);
            PropertyDescriptor[] destProperty = destBean.getPropertyDescriptors();


            for(int i=0;i<sourceProperty.length;i++){
                for(int j=0;j<destProperty.length;j++){
                    if(sourceProperty[i].getName().equals(destProperty[j].getName()) && sourceProperty[i].getPropertyType().getName().equals(destProperty[j].getPropertyType().getName())){
                        //调用source的getter方法和dest的setter方法
                        destProperty[j].getWriteMethod().invoke(dest, sourceProperty[i].getReadMethod().invoke(source));
                        break;
                    }
                }
            }
        }catch(Exception e){
            logger.info("属性复制失败:"+e.getMessage());
        }
    }

    public static String fillTobankCard(String bankCardNumber){
        if(bankCardNumber == null) return "";
        StringBuilder sb=new StringBuilder(bankCardNumber.substring(0,1));
        for(int i=0;i<bankCardNumber.length()-5;i++){
            sb.append("*");
        }
        sb.append(bankCardNumber.substring(bankCardNumber.length()-4));
        return sb.toString();
    }

    public static BigDecimal centToDollar(Long value) {
        if(value == null) return null;
        return new BigDecimal(value).movePointLeft(2).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal centToDollar(BigDecimal value) {
        if(value == null) return null;
        return value.movePointLeft(2).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal centToDollarIgnoreNull(BigDecimal value) {
        if(value == null) return BigDecimal.ZERO.movePointLeft(2).setScale(2, RoundingMode.HALF_UP);
        return value.movePointLeft(2).setScale(2, RoundingMode.HALF_UP);
    }

    public static void writeCSV(HttpServletResponse response, String csvFileName, String data) throws ApiException {
        if(response == null) throw new ApiException("error.in.export.csv");

        response.setContentType("text/csv; charset=UTF-8");
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", csvFileName);
        response.setHeader(headerKey, headerValue);

        try {
            response.getWriter().write(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                response.getWriter().close();
            } catch (IOException e) {
                logger.debug(e.getMessage());
                throw new ApiException("error.in.export.csv");
            }
        }
    }

    public static String imgToBase64String(final BufferedImage img, final String formatName) throws ApiException {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            ImageIO.write(img, formatName, os);
            return java.util.Base64.getEncoder().encodeToString(os.toByteArray());
        } catch (final IOException ioe) {
            throw new ApiException("error.in.convert.image.to.base64.string");
        }
    }

    public static void downloadFile(File file, HttpServletResponse response, boolean isDelete) {
        try {
            // 以流的形式下载文件。
            BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file.getPath()));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String(file.getName().getBytes("UTF-8"), "ISO-8859-1"));
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
            if (isDelete) {
                file.delete(); // 是否将生成的服务器端文件删除
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void zip(String inputFileName, String zipFileName) throws Exception {
        zip(zipFileName, new File(inputFileName));
    }

    private static void zip(String zipFileName, File inputFile) throws Exception {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
        zip(out, inputFile, "");
        out.close();
    }

    private static void zip(ZipOutputStream out, File f, String base) throws Exception {
        if (f.isDirectory()) { // 判断是否为目录
            File[] fl = f.listFiles();
            out.putNextEntry(new ZipEntry(base + "/"));
            base = base.length() == 0 ? "" : base + "/";
            for (int i = 0; i < fl.length; i++) {
                zip(out, fl[i], base + fl[i].getName());
            }
        } else { // 压缩目录中的所有文件
            out.putNextEntry(new ZipEntry(base));
            FileInputStream in = new FileInputStream(f);
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            in.close();
        }
    }

    public static String getBaseUrl(HttpServletRequest request) {
        try {
            String serverAddress = request.getServerName();
            if("localhost".equals(serverAddress) || "127.0.0.1".equals(serverAddress) || "0.0.0.0".equals(serverAddress)) {
                serverAddress = InetAddress.getLocalHost().getHostAddress();
            }
            if(request.getServerPort() == 80) {
                return String.format("https://%s/", serverAddress);
            } else {
                return String.format("%s://%s:%d/",request.getScheme(), serverAddress, request.getServerPort());
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getLocalIP() {
        try {
            Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            String localIP = "";
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = (InetAddress) addresses.nextElement();
                    if (ip != null && !ip.isLoopbackAddress() && ip instanceof Inet4Address) {
                        localIP = ip.getHostAddress();
                        break;
                    }
                }
            }
            return localIP;
        } catch (SocketException e) {
            return "";
        }
    }

    public static Boolean isEmailAddressList(String str) {
        if(isEmpty(str)) return false;
        return str.matches("^([\\w+-.%]+@[\\w-.]+\\.[A-Za-z]{2,4}[\\s,;]*)+$");
    }

    public static String joinPath(String path1, String path2) {
        if(isEmpty(path1)) return path2;
        if(path1.endsWith("/")) {
            return path1 + path2;
        } else {
            return path1 + "/" + path2;
        }
    }

    public static RestTemplate getRestTemplate() {
        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

        try {
            SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
            SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setHttpClient(httpClient);
            requestFactory.setConnectTimeout(6000);
            requestFactory.setReadTimeout(6000);
            return new RestTemplate(requestFactory);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return null;
    }
}
