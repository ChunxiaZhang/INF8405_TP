package com.polymtl.jiajing.tp2_localisationmap.model;

import android.content.Context;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;


/**
 * Created by Zoe on 15-02-23.
 */
public class ConnectNetworkInfo implements ConnectInfo {

    private Context context;
    private TelephonyManager telephonyManager;
    private CdmaCellLocation cdmaCellLocation;
    private int networkType;
    private int phoneType;
    private String MCC; //Mobile country code
    private String MNC; //Mobile Network cod
    private String operatorName;
    private float long_sb, lat_sb;
    private int Niv_sig_sb; //niveau du signal

    public ConnectNetworkInfo(Context context) {
        this.context = context;
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        cdmaCellLocation = (CdmaCellLocation) telephonyManager.getCellLocation();
    }

    public int getNetworkType() {
        this.networkType = telephonyManager.getNetworkType(); //NETWORK_TYPE_CDMA, NETWORK_TYPE_UMTS
        return this.networkType;
    }

    public int getPhoneType() {
        this.phoneType = telephonyManager.getPhoneType(); //PHONE_TYPE_CDMA, PHONE_TYPE_GSM
        return this.getPhoneType();
    }

    public String getMCC() {
        if (telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
            this.MCC = String.valueOf(cdmaCellLocation.getSystemId());

        } else {
            this.MCC = telephonyManager.getNetworkOperator().substring(0, 3);
        }
        return this.MCC;
    }

    public String getMNC() {
        if (telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
            this.MNC = String.valueOf(cdmaCellLocation.getSystemId());
        } else {
            this.MNC = telephonyManager.getNetworkOperator().substring(3, 5);
        }

        return this.MNC;
    }

    public String getOperatorName() {

        if (telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
            this.operatorName = String.valueOf(cdmaCellLocation.getSystemId());
        } else {
            this.operatorName = telephonyManager.getNetworkOperatorName();
        }

        return this.operatorName;
    }

    public float getLong_sb() {
        /**
         * Longitude is a decimal number as specified in 3GPP2 C.S0005-A v6.0.
         * (http://www.3gpp2.org/public_html/specs/C.S0005-A_v6.0.pdf)
         * It is represented in units of 0.25 seconds and ranges from -2592000 to 2592000,
         * both values inclusive (corresponding to a range of -180 to +180 degrees).
         * Integer.MAX_VALUE is considered invalid value.
         */
        if(telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
            this.long_sb = cdmaCellLocation.getBaseStationLongitude();
        }
        //else ???
        return this.long_sb;
    }

    public float getLat_sb() {
        /**
         * Latitude is a decimal number as specified in 3GPP2 C.S0005-A v6.0.
         * (http://www.3gpp2.org/public_html/specs/C.S0005-A_v6.0.pdf)
         * It is represented in units of 0.25 seconds and ranges from -1296000 to 1296000,
         * both values inclusive (corresponding to a range of -90 to +90 degrees).
         * Integer.MAX_VALUE is considered invalid value.
         */
        if (telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
            this.lat_sb = cdmaCellLocation.getBaseStationLatitude();
        }
        //else ???
        return this.lat_sb;
    }

    public int getNiv_sig_sb() {
        for (CellInfo cellInfo : telephonyManager.getAllCellInfo()) {
            if (cellInfo instanceof CellInfoCdma) {
                final CellSignalStrengthCdma cellSignalStrengthCdma = ((CellInfoCdma) cellInfo).getCellSignalStrength();
                //Niv_sig_sb = cellSignalStrengthCdma.getLevel(); //Get signal level as an int from 0..4
                this.Niv_sig_sb = cellSignalStrengthCdma.getDbm(); //Get the signal strength as dBm
            } else if (cellInfo instanceof CellInfoGsm) {
                final CellSignalStrengthGsm cellSignalStrengthGsm = ((CellInfoGsm) cellInfo).getCellSignalStrength();
                //Niv_sig_sb = cellSignalStrengthGsm.getLevel(); //Get signal level as an int from 0..4
                this. Niv_sig_sb = cellSignalStrengthGsm.getDbm(); //Get the signal strength as dBm
            } else if (cellInfo instanceof CellInfoLte) {
                final CellSignalStrengthLte cellSignalStrengthLte = ((CellInfoLte) cellInfo).getCellSignalStrength();
                this.Niv_sig_sb = cellSignalStrengthLte.getDbm();
            } else if (cellInfo instanceof CellInfoWcdma) {
                final CellSignalStrengthWcdma cellSignalStrengthWcdma =
                        ((CellInfoWcdma) cellInfo).getCellSignalStrength();
                this. Niv_sig_sb = cellSignalStrengthWcdma.getDbm();
            }

        }
        return this.Niv_sig_sb;
    }

    //What's the format  ????????????????
    @Override
    public String getConnectInfo() {
        return null;
    }
}
