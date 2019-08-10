package com.example.demo.controllers;

import com.example.demo.helpers.VWOHelper;
import com.vwo.VWO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AbTestController {

    String currentSettingsFile;
    VWO vwoInstance;
    Number pollTime = 1000;
    String accountId = ""; // Please provide you accountId here
    String sdkKey = ""; // Please provide you sdk-key here

    private AbTestController() {
        this.fetchSettingsAndCreateInstance();
    }

    private void fetchSettingsAndCreateInstance() {
        String settingsFile = VWOHelper.getSettingsFile(this.accountId, this.sdkKey);

        if (settingsFile == null || !settingsFile.equals(this.currentSettingsFile)) {
            this.currentSettingsFile = settingsFile;
            this.vwoInstance = VWO.createInstance(settingsFile).withUserProfileService(VWOHelper.getUserProfileService()).withCustomLogger(VWOHelper.getCustomLogger()).build();
        }
    }

    @GetMapping(value = "/")
    public String trackUser(
        @RequestParam(value = "userId", required = false) String userId,
        Model model
    ) {
        userId = userId == null ? VWOHelper.getRandomUser() : userId;

        String campaignTestKey = "FIRST";
        String goalIdentifier  = "CUSTOM"; // "REVENUE"

        String variation = this.vwoInstance.activate(campaignTestKey, userId);
        this.vwoInstance.track(campaignTestKey, userId, goalIdentifier);

        model.addAttribute("title", "VWO | Java-sdk example | " + variation);
        model.addAttribute("userId", userId);
        model.addAttribute("isPartOfCampaign", variation != null);
        model.addAttribute("variation", variation);
        model.addAttribute("campaignTestKey", campaignTestKey);
        model.addAttribute("goalIdentifier", goalIdentifier);
        model.addAttribute("settingsFile", VWOHelper.prettyJsonSting(this.currentSettingsFile));

        return "index";
    }
}
