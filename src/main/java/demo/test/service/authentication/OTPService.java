package demo.test.service.authentication;

import demo.test.constant.AuthenticationEnum;
import demo.test.model.entity.OTPEntity;
import demo.test.repository.OTPRepository;
import demo.test.service.utilities.EmailService;
import demo.test.util.NumberUtils;
import demo.test.util.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
public class OTPService {

    @Autowired
    private OTPRepository otpRepository;

    @Autowired
    private EmailService emailService;

    public AuthenticationEnum createForMail(String mail) {
        String randomOTP = NumberUtils.generateRandomString(6);
        OTPEntity otpEntity = new OTPEntity(mail, randomOTP, TimeUtils.getCurrentTimestamp());

        //send OTP to mail
        boolean didSendOTP = emailService.sendSimpleEmail(mail, "Test OTP", "Your OTP is " + randomOTP);
        if (!didSendOTP) {
            return AuthenticationEnum.INVALID_EMAIL;
        }

        //Save to db
        otpRepository.save(otpEntity);
        return AuthenticationEnum.SEND_OTP_SUCCESS;
    }


    public AuthenticationEnum verifyOtpForEmail(String email, String otp) {
        try {
            OTPEntity userOTP = otpRepository.getById(email);
            AuthenticationEnum status = checkValidOtp(otp, userOTP);

            switch (status) {
                case SUCCESS:
                case END_OF_TRY:
                case TIME_OUT:
                    removeForEmail(email);
                    break;
                case WRONG:
                    decreaseAttempt(userOTP);
                    break;
                default:
            }

            return status;
        } catch (EntityNotFoundException ignored) {
            return AuthenticationEnum.NOT_FOUND;
        }
    }

    public void removeForEmail(String email) {
        otpRepository.deleteById(email);
    }

    private void decreaseAttempt(OTPEntity otpEntity) {
        otpEntity.decreaseRetryTime();
        otpRepository.save(otpEntity);
    }

    private AuthenticationEnum checkValidOtp(String otp, OTPEntity otpEntity) {

        if (otpEntity.getTimeRetry() < 0) {
            return AuthenticationEnum.END_OF_TRY;
        }

        if (!otpEntity.getOTP().equals(otp)) {
            return AuthenticationEnum.WRONG;
        }

        long currentTime = TimeUtils.getCurrentTimestamp();
        if (currentTime - otpEntity.getCreateTime() > 60 * 1000 * 60) {     //1 hour
            return AuthenticationEnum.TIME_OUT;
        }

        return AuthenticationEnum.SUCCESS;
    }

    public int responseVerifyOtpEmail(String email, String otp) {
        if (email != null) {
            OTPEntity otpEntity = otpRepository.getById(email);
            if (otpEntity.getTimeRetry() == 0) {
                return 2;
            } else {
                if (otpEntity.getOTP().equals(otp)) {
                    return 3;
                } else {
                    otpEntity.decreaseRetryTime();
                    return 4;
                }
            }

        }
        return 1;
    }
}