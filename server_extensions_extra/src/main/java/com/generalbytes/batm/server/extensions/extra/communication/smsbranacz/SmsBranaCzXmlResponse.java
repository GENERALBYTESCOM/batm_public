package com.generalbytes.batm.server.extensions.extra.communication.smsbranacz;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * XML response object for SMSBrána.cz API calls using JAXB
 * Example XML response:
 * <result>
 * <err>0</err>
 * <price>1.1</price>
 * <sms_count>1</sms_count>
 * <credit>1523.32</credit>
 * <sms_id>377351</sms_id>
 * </result>
 */
@Setter
@Getter
@ToString
@XmlRootElement(name = "result")
@XmlAccessorType(XmlAccessType.FIELD)
public class SmsBranaCzXmlResponse {

    @XmlElement(name = "err")
    private Integer err;

    @XmlElement(name = "price")
    private String price;

    @XmlElement(name = "sms_count")
    private Integer smsCount;

    @XmlElement(name = "credit")
    private String credit;

    @XmlElement(name = "sms_id")
    private Long smsId;

}