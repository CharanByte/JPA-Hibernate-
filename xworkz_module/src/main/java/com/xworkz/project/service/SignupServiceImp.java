package com.xworkz.project.service;

import com.xworkz.project.dto.SignupDTO;
import com.xworkz.project.entity.SignupEntity;
import com.xworkz.project.repository.SignupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import java.util.Random;

@Service
public class SignupServiceImp implements SignupService {

    @Autowired
    SignupRepository signupRepository;

    String generatedPassword;

    public String passwordGenerate() {
        String Capital_chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String Small_chars = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";


        String values = Capital_chars + Small_chars + numbers;

        Random rndm = new Random();

        StringBuilder password = new StringBuilder();

        for (int i = 0; i < 6; i++) {

            password.append(values.charAt(rndm.nextInt(values.length())));

        }
        return password.toString();
    }


    @Override
    public boolean valid(SignupDTO signupDTO) {
        boolean valid = false;


        if (signupDTO != null && signupDTO.getEmail() != null) {
            SignupEntity signupEntity = new SignupEntity();
            generatedPassword = passwordGenerate();
           
            signupEntity.setName(signupDTO.getName());
            signupEntity.setEmail(signupDTO.getEmail());
            signupEntity.setPassword(generatedPassword);
            signupEntity.setPhoneNo(signupDTO.getPhoneNo());
            signupEntity.setAltEmail(signupDTO.getAltEmail());
            signupEntity.setAltPhhoneNo(signupDTO.getAltPhoneNo());
            signupEntity.setLocation(signupDTO.getLocation());
            signupEntity.setNo(-1);
            signupEntity.setCreatedBy(signupDTO.getName());
            signupEntity.getCreatedDate();

            valid = true;

            signupRepository.save(signupEntity);
        }
        return valid;
    }

    @Override
    public boolean validateSigninDetails(String name, String password) {
        boolean passwordMaches = true;
        String passwordFromDb = signupRepository.getUserName(name);
        if (password != null) {
            if (password.equals(passwordFromDb))
                System.out.println("password matching");
            else {
                System.out.println("password not matching");
                passwordMaches = false;
            }
        }
        return passwordMaches;
    }

    @Override
    public int checkUserName(SignupDTO signupDTO) {

        return signupRepository.checkUserName(signupDTO);
    }

    @Override
    public long getCountOfName(String name) {

        return signupRepository.getCountOfName(name);
    }

    @Override
    public boolean validateUserName(String name, String oldPassword) {
        boolean isEqual = true;
        String[] getUserName = signupRepository.validateUserName(name);
        System.out.println("getUserName" + getUserName[0]);
        if (name.equals(getUserName[0]) && oldPassword.equals(getUserName[1]) && getUserName[2].equals("-1")) {
            System.out.println("equal");
        } else {
            System.out.println("User name is incorrect");
            isEqual = false;
        }

        return isEqual;
    }

    @Override
    public int updatePassword(String name, String newPassword, String confirmPassword) {
        int value = 0;
        if (newPassword.equals(confirmPassword)) {
            System.out.println("newPassword and comfirmPassword is equal");
            value = signupRepository.updatePassword(name, newPassword);
            System.out.println(value);
        } else {
            System.out.println("newPassword and comfirmPassword is notEqual");
        }
        return value;
    }

    @Override
    public Long getCountOfUserName(String name) {

        return signupRepository.getCountOfUserName(name);
    }

    @Override
    public int getCountValue(String name, String password) {
        return signupRepository.getCountValue(name, password);

    }


    @Override
    public int getAll(String name, String password) {

        SignupEntity signupEntity = signupRepository.getAll(name, password);
        System.out.println(signupEntity.toString());
        if (signupEntity != null) {
            if (name.equals(signupEntity.getName()) && password.equals(signupEntity.getPassword()) && signupEntity.getNo() >= 0 && signupEntity.getNo() <= 3) {
                System.out.println("successfully signined");
                signupRepository.updateCountBy1(name, 0);
                return 1;
            } else if (name.equals(signupEntity.getName()) && password.equals(signupEntity.getPassword()) && signupEntity.getNo() >= 3) {
                LocalDateTime a = signupEntity.getLockedtime();
                LocalDateTime b = a.plusMinutes(2);
                LocalDateTime time = LocalDateTime.now();
                if (time.isAfter(b)) {
                    System.out.println("successfully signined");
                    signupRepository.updateCountBy1(name, 0);
                    return 1;
                } else {
                    System.out.println("try after 2 min");
                }
                return 3;

            } else if (name.equals(signupEntity.getName()) && !password.equals(signupEntity.getPassword()) && signupEntity.getNo() >= 0 && signupEntity.getNo() < 3) {
                System.out.println("incorect password count increase by 1 ");
                signupRepository.updateCountBy1(name, signupEntity.getNo() + 1);
                return 2;

            } else if (name.equals(signupEntity.getName()) && !password.equals(signupEntity.getPassword()) && signupEntity.getNo() >= 0 && signupEntity.getNo() >= 3) {
                System.out.println("locked");
                signupRepository.updateCountBy1(name, signupEntity.getNo() + 1);
                if (signupEntity.getNo() == 3) {
                    LocalDateTime localDateTime = LocalDateTime.now();
                    System.out.println(localDateTime);
                    signupRepository.updateLockTime(name, localDateTime);
                }
                return 3;
            }

        }


        return 0;
    }

    @Override
    public boolean saveEmail(String email) {

        final String username = "charan7812@gmail.com";
        final String userPassword = "arut fpac cnld mnlh";

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); //TLS

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, userPassword);
                    }
                });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(email)
            );
            message.setSubject("Your password");
            message.setText("your password : " + generatedPassword);

            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public int updateExistingDetails(SignupDTO signupDTO, String imageProfile) {
        SignupEntity signupEntity = new SignupEntity();
        signupEntity.setName(signupDTO.getName());
        signupEntity.setEmail(signupDTO.getEmail());
        signupEntity.setPhoneNo(signupDTO.getPhoneNo());
        signupEntity.setAltEmail(signupDTO.getAltEmail());
        signupEntity.setAltPhhoneNo(signupDTO.getAltPhoneNo());
        signupEntity.setLocation(signupDTO.getLocation());
        signupEntity.setUpdatedBy(signupDTO.getName());
        signupEntity.getUpdatedBy();
        signupEntity.setImageProfile(imageProfile);
        return signupRepository.updateExistingDetails(signupEntity);

    }

    @Override
    public SignupEntity getAllDetails(String name, String password) {
        SignupEntity signupEntity = signupRepository.getAll(name, password);
        return signupEntity;
    }

    @Override
    public List<String> getAllUserName() {
        return signupRepository.getAllUserName();
    }


    @Override
    public void updateCount(String name, int i) {
        signupRepository.updateCountBy1(name, 0);
    }

    @Override
    public SignupEntity findAllByName(String name) {
        return signupRepository.findAllByName(name);

    }
}


