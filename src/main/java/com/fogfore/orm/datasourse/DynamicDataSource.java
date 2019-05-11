package com.fogfore.orm.datasourse;

import com.fogfore.orm.datasourse.DynamicDataSourceEntry;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {
    private DynamicDataSourceEntry dataSourceEntry;

    @Override
    protected Object determineCurrentLookupKey() {
        return this.dataSourceEntry.get();
    }

    public DynamicDataSourceEntry getDataSourceEntry() {
        return dataSourceEntry;
    }

    public void setDataSourceEntry(DynamicDataSourceEntry dataSourceEntry) {
        this.dataSourceEntry = dataSourceEntry;
    }
}
