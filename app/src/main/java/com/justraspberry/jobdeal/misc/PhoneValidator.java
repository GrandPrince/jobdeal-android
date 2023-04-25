package com.justraspberry.jobdeal.misc;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class PhoneValidator {

    public static String validate( String countryCode, String phoneNumber, String region) {

        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance() ;
        try {
            Phonenumber.PhoneNumber phone;
            if (!region.equalsIgnoreCase("")) {
                phone = phoneNumberUtil.parse(phoneNumber, region.toUpperCase());
            } else {
                phone = phoneNumberUtil.parse(phoneNumber, phoneNumberUtil.getRegionCodeForCountryCode(Integer.parseInt(countryCode)));
            }

            if (phoneNumberUtil.isValidNumber(phone)) {
                if (!region.equalsIgnoreCase(""))
                    return phoneNumberUtil.format(phone, PhoneNumberUtil.PhoneNumberFormat.E164).replace("+" + getNumberCode(region.toUpperCase()), "");
                else
                    return phoneNumberUtil.format(phone, PhoneNumberUtil.PhoneNumberFormat.E164).replace("+" + countryCode, "");
            }
        } catch (Exception e) {
            return null;
        }

        return null;
    }

    public static String getNumberCode(String region) {
        return String.valueOf(PhoneNumberUtil.getInstance().getCountryCodeForRegion(region.toUpperCase()));
    }

    public static String getExampleNumber(String region) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        return phoneNumberUtil.format(phoneNumberUtil.getExampleNumberForType(region.toUpperCase(), PhoneNumberUtil.PhoneNumberType.MOBILE), PhoneNumberUtil.PhoneNumberFormat.E164);
    }

    public static String getPhoneNumberWithRegion(String countryCode, String phoneNumber, String region){
        return "+" + getNumberCode(region) + validate(countryCode,phoneNumber,region);
    }

}
