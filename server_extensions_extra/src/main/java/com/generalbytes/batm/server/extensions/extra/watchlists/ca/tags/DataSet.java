package com.generalbytes.batm.server.extensions.extra.watchlists.ca.tags;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "data-set")
@XmlAccessorType(XmlAccessType.FIELD)
public class DataSet {

    @XmlElement(name = "record")
    private List<Record> records;

    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }
}
