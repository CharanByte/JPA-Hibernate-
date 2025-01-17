package com.xworkz.project.controller;

import com.xworkz.project.constants.LocationEnum;
import com.xworkz.project.entity.SignupEntity;
import com.xworkz.project.service.SignupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/")
public class SigninController {

    @Autowired
    private SignupService signupService;

    List<LocationEnum> locationEnums = new ArrayList<>(Arrays.asList(LocationEnum.values()));


    SigninController() {
        System.out.println("SigninController");
    }

    @GetMapping("/signin")
    public String signin(Model model, @RequestParam String name) {
        System.out.println(name);
        SignupEntity signupEntity = signupService.findAllByName(name);
        System.out.println("signupEntity :" + signupEntity);
        model.addAttribute("location", locationEnums);

        model.addAttribute("signupEntity", signupEntity);
        return "UpdateUserDetails.jsp";
    }

    @PostMapping("/signin")
    public String getSignin(String name, String password, Model model) {

        boolean matches = signupService.validateSigninDetails(name, password);
        if (matches) {
            signupService.getAllDetails(name, password);
        }

        int value = signupService.getAll(name, password);
        System.out.println(value);
        if (value == 2) {
            model.addAttribute("failure", "incorrect password");
            return "Signin.jsp";

        } else if (value == 3) {
            model.addAttribute("locked", "your account is locked");
            return "Signin.jsp";
        }
        int count = signupService.getCountValue(name, password);
        if (matches && count >= 0) {

            model.addAttribute("success", "Successfully SignIn as " + name);
            model.addAttribute("name", name);
            return "Success.jsp";
        } else if (matches && count == -1) {
            return "PasswordReset.jsp";
        }
        model.addAttribute("failure", "incorrect password");
        model.addAttribute("dto", name);
        return "Signin.jsp";
    }


    @PostMapping("/forgotPassword")
    public String forgotPassword(String userName, String newPassword, String confirmNewPassword, Model model) {
        System.out.println(userName);
        List<String> userNames = signupService.getAllUserName();

        System.out.println(userNames);
        if (userNames.contains(userName)) {

            int value = signupService.updatePassword(userName, newPassword, confirmNewPassword);
            if (value != 0) {
                model.addAttribute("passwordUpdated", userName + " Your password has been successfully reset");
            }
        } else {
            model.addAttribute("invalidUser", "invalid UserName");
        }
        return "forgotPassword.jsp";
    }


}
