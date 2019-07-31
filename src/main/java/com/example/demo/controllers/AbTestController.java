package com.example.demo.controllers;



import com.vwo.VWO;
import com.vwo.config.FileSettingUtils;
import com.vwo.models.Variation;
import com.vwo.userprofile.UserProfileService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

@RestController
public class AbTestController {

    VWO vwo=null;
    VWO vwo_instance;

    @RequestMapping(value = "/v1/settingFile", method = RequestMethod.GET ,produces={"application/json"})
    public String getSettings(@RequestParam(value = "account_id") String accountId,
                              @RequestParam(value = "sdk_key") String sdkKey,
                              HttpServletResponse response) throws Exception {
        String settingsFile = FileSettingUtils.getSetting(accountId, sdkKey);
        if(settingsFile!=null){
            response.setStatus(201);
            return settingsFile;
        }
        else{
            response.sendError(400,"Could not fetch setting file");
            return null;
        }
    }

    @RequestMapping(value = "/v1/java/sdk", method = RequestMethod.GET ,produces={"application/json"})
    public String initialize(HttpServletResponse response) throws Exception {


        if(vwo_instance==null) {
            String settingsFile = FileSettingUtils.getSetting("", "");
            if (settingsFile != null) {
                response.setStatus(201);
            } else {
                response.sendError(400, "Could not fetch setting file");
                return null;
            }
            vwo_instance = VWO.createInstance(settingsFile).build();
        }
        String name= getRandomName();
        String variation=null;
        String data;
        if(vwo_instance==null){
            data="Invalid vwo instance!! Please initialize vwo instance!!";
            return data;
        }else{
            System.out.println("User ID ----> :"+name);
            variation= vwo_instance.activate("FIRST",name);
        }
        if(variation!=null) {
            data = "\n \n User Id ::"+name+"\n\n Variation name::"+variation;
            return data;
        }
        data = "\n \n User Id ::"+name+"\n\n Variation name:: No varation assigned.";
        return data;
    }

    @RequestMapping(value = "/v1/init", method = RequestMethod.POST)
    @ResponseBody
    public void init(@RequestBody String payload)
                         throws Exception {
        System.out.println(payload);
        UserProfileService userProfileService= new UserProfileService() {
            @Override
            public Map<String, Object> lookup(String userId, String campaignId) throws Exception {

                //to do
                String variationId = "Control";

                Map<String,Object> campaignKeyMap = new HashMap<>();
                Map<String, String> variationKeyMap = new HashMap<>();
                variationKeyMap.put(UserProfileService.variationKey, variationId);
                campaignKeyMap.put(campaignId,variationKeyMap);

                //set
                Map<String, Object> campaignStaticBucketMap = new HashMap<>();
                campaignStaticBucketMap.put(UserProfileService.userId,"Priya");
                campaignStaticBucketMap.put(UserProfileService.campaignKey, campaignKeyMap);

              return campaignStaticBucketMap;
            }

            @Override
            public void save(Map<String, Object> map) throws Exception {

            }
        };
        this.vwo = VWO.createInstance(payload)
                      .withUserProfileService(userProfileService)
                      .build();
    }


    @RequestMapping(value = "/v1/activate", method = RequestMethod.GET)
    public String activate(@RequestParam String ckey,
                                     @RequestParam String user_id) throws Exception {
        String  variation;
        String data=null;
      if(vwo==null){
          data="Invalid vwo instance!! Please initialize vwo instance!!";
          return data;
      }else{
        variation= vwo.activate(ckey,user_id);
      }
      if(variation!=null) {
          data=variation;
          return data;
      }
        data = "No varation assigned.";
      return data;
    }

    @RequestMapping(value = "/v1/track", method = RequestMethod.GET)
    public String track(@RequestParam String ckey,@RequestParam String user_id,@RequestParam String goal_id)
            throws Exception {
        boolean variation;
        String data=null;
        if(vwo==null){
            data="Invalid vwo instnace!! Please initialize vwo instance!!";
            return data;
        }else{
            variation = vwo.track(ckey,user_id,goal_id);
        }
        if(variation==false) {
            data = "No variation assigned";
        }else {
            data = "CALL SUCCESSFULLY DISPATCHED!!";
        }
        return data;
    }


    public String getRandomName(){
        Random rand = new Random();
        LinkedList<String> linkedList = new LinkedList<>();
        linkedList.add("Ankit");
        linkedList.add("shubham");
        linkedList.add("bbkbkbk");
        linkedList.add("varun");
        linkedList.add("sakshi");
        linkedList.add("Priya");
        linkedList.add("gaurav");
        return linkedList.get(rand.nextInt(linkedList.size()));


    }
    }
