package com.replate.exception;

public class DonationExpiredException extends ReplateException {

    public DonationExpiredException(String message) {
        super(message, "DONATION_EXPIRED");
    }

    public DonationExpiredException(Long donationId) {
        super("The donation with ID " + donationId + " has surpassed its safe consumption expiry threshold.", "DONATION_EXPIRED");
    }
}

