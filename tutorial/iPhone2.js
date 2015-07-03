{"id":"iPhone","elements":[{"id":"e1a7b438-5b0b-4988-887e-4c5e89415980","action":{"actionType":"Message","params":{"message":"TUTORIAL_BATTLES_1","form":"full"}},"conditions":[{"conditionType":"ProfileVariableState","params":{"profileFieldPath":"analytics.secondsFromProfileCreation","predicate":">= 28800"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"dd4ae2b2-b881-4c61-8ba3-0fcd3553e4a1","action":{"actionType":"AssignTutorialTask","params":{"tutorialTaskId":"3f68c975-d356-4047-b525-093e83dda8a9"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"e1a7b438-5b0b-4988-887e-4c5e89415980"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"578ca718-7f03-4839-9b38-c3a3e1eeee8f","action":{"actionType":"IncTutorialTask","params":{"tutorialTaskId":"3f68c975-d356-4047-b525-093e83dda8a9"}},"conditions":[{"conditionType":"ScreenOpened","params":{"screenId":"TimelineScreen"}},{"conditionType":"TutorialTaskActive","params":{"tutorialTaskId":"3f68c975-d356-4047-b525-093e83dda8a9"}},{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"dd4ae2b2-b881-4c61-8ba3-0fcd3553e4a1"}}],"triggers":[{"triggerType":"OurBattleIsShown","params":{}}]},{"id":"b64e66f6-2bd1-4d97-8d18-8ba6aaa0a7fa","action":{"actionType":"ScrollToNearestBattleInTimeLine","params":{}},"conditions":[{"conditionType":"ScreenOpened","params":{"screenId":"TimelineScreen"}},{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"578ca718-7f03-4839-9b38-c3a3e1eeee8f"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"5042cb72-0081-47a4-ab53-75596344ac16","action":{"actionType":"FocusOnGUIElement","params":{"guiElementId":"BattleBlock"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"b64e66f6-2bd1-4d97-8d18-8ba6aaa0a7fa"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"031a6e9d-cd64-4b20-9903-93ac94ef6b17","action":{"actionType":"Message","params":{"message":"TUTORIAL_BATTLES_2","form":"mini","vAligment":"bottom"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"b64e66f6-2bd1-4d97-8d18-8ba6aaa0a7fa"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"5acba63b-cfdc-449d-a91d-a64b0450cbc2","action":{"actionType":"Message","params":{"message":"TUTORIAL_BATTLES_3","form":"full"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"031a6e9d-cd64-4b20-9903-93ac94ef6b17"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"5d2f661d-92b5-49b7-b883-4ba70bf8a675","action":{"actionType":"Message","params":{"message":"TUTORIAL_BATTLES_4","form":"full"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"5acba63b-cfdc-449d-a91d-a64b0450cbc2"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"2896b749-a043-40c9-819e-bd29881eadaa","action":{"actionType":"Message","params":{"message":"TUTORIAL_BATTLES_5","form":"full"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"5d2f661d-92b5-49b7-b883-4ba70bf8a675"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"72142616-bc46-498f-bbf8-8ac0021e2ce5","action":{"actionType":"RemoveFocus","params":{"guiElementId":"BattleBlock"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"2896b749-a043-40c9-819e-bd29881eadaa"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"8d9c8115-3062-4f4e-b967-0873876088de","action":{"actionType":"Message","params":{"message":"TUTORIAL_CHALLENGES_1","form":"full"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"2896b749-a043-40c9-819e-bd29881eadaa"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"dd46514f-8726-4350-aaf2-7dde641dd7d6","action":{"actionType":"AssignTutorialTask","params":{"tutorialTaskId":"ef84c907-8056-4175-b7ba-1537881707c7"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"8d9c8115-3062-4f4e-b967-0873876088de"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"aa9b26b7-7ef7-4763-a1b6-95b4cd95cfe4","action":{"actionType":"FocusOnGUIElement","params":{"guiElementId":"SolveQuestButton"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"dd46514f-8726-4350-aaf2-7dde641dd7d6"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"c48472c5-5f53-4406-9be7-bf6fac007c9d","action":{"actionType":"RemoveFocus","params":{"guiElementId":"SolveQuestButton"}},"conditions":[{"conditionType":"TutorialTaskCompleted","params":{"tutorialTaskId":"ef84c907-8056-4175-b7ba-1537881707c7"}},{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"aa9b26b7-7ef7-4763-a1b6-95b4cd95cfe4"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"a3afbdd0-0fd0-4b2f-89b5-28d927f95a80","action":{"actionType":"Message","params":{"message":"TUTORIAL_CHALLENGES_2","form":"mini","vAligment":"bottom"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"dd46514f-8726-4350-aaf2-7dde641dd7d6"}},{"conditionType":"ProfileVariableState","params":{"profileFieldPath":"publicProfile.level","predicate":">= 3"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"2f0cd883-e2f8-429f-a98a-504282539107","action":{"actionType":"Message","params":{"message":"TUTORIAL_CHALLENGES_3","form":"full"}},"conditions":[{"conditionType":"ScreenOpened","params":{"screenId":"TimelineScreen"}},{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"a3afbdd0-0fd0-4b2f-89b5-28d927f95a80"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"8e3a32a6-b7e4-4607-865d-3e4ff98904cd","action":{"actionType":"FocusOnGUIElement","params":{"guiElementId":"ChallengeBattleButton"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"2f0cd883-e2f8-429f-a98a-504282539107"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"0cd18d13-c4d6-444b-b66f-354db8a0aec7","action":{"actionType":"AssignTutorialTask","params":{"tutorialTaskId":"74505643-d1ef-450d-b781-578c08c65b51"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"8e3a32a6-b7e4-4607-865d-3e4ff98904cd"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"d55b0d10-6707-442c-9a3b-7e827f274866","action":{"actionType":"RemoveFocus","params":{"guiElementId":"ChallengeBattleButton"}},"conditions":[{"conditionType":"TutorialTaskCompleted","params":{"tutorialTaskId":"74505643-d1ef-450d-b781-578c08c65b51"}},{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"0cd18d13-c4d6-444b-b66f-354db8a0aec7"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"2efdbb55-4632-44e7-ba60-be399a129edf","action":{"actionType":"Message","params":{"message":"TUTORIAL_CHALLENGES_4","form":"full"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"d55b0d10-6707-442c-9a3b-7e827f274866"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"1fb7780f-4a1b-4756-b421-0114cf796c0d","action":{"actionType":"Message","params":{"message":"TUTORIAL_END_DAY_2","form":"full"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"2efdbb55-4632-44e7-ba60-be399a129edf"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"706741eb-33c6-4bfa-95da-51dcf0d2f258","action":{"actionType":"AssignTutorialTask","params":{"tutorialTaskId":"ef48345c-5d48-4468-ade4-630859fe0e70"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"1fb7780f-4a1b-4756-b421-0114cf796c0d"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"a61b5909-4c03-4a6d-aea2-0b7be741aa42","action":{"actionType":"AssignTutorialTask","params":{"tutorialTaskId":"706741eb-33c6-4bfa-95da-51dcf0d2f258"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"706741eb-33c6-4bfa-95da-51dcf0d2f258"}}],"triggers":[{"triggerType":"Any","params":{}}]}]}