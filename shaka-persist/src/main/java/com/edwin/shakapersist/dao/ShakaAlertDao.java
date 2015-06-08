package com.edwin.shakapersist.dao;

import com.edwin.shakapersist.entity.ShakaAlert;

/**
 * Created by shichao.liao on 15/5/22.
 */
public interface ShakaAlertDao {

    public int addShakaAlert(ShakaAlert shakaAlert);
    public ShakaAlert loadShakaAlert(int alertId);
}
