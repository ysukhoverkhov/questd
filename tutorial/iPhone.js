{"id":"iPhone","elements":[{"id":"112ee64a-671b-4670-9f0e-b1299f2e9350","action":{"actionType":"Message","params":{"message":"TUTORIAL_HELLO_1","form":"full"}},"conditions":[],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"b4550e0c-6337-4348-8c5a-f1ef880d88d8","action":{"actionType":"Message","params":{"message":"TUTORIAL_EARLY_ACCESS_NOTE","form":"full"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"33c14727-370c-43e0-9d34-590cfadf97c4"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"b747ec32-7688-4095-94b7-2884d3531f4b","action":{"actionType":"Message","params":{"message":"TUTORIAL_LOCATION_SELECTION","form":"full"}},"conditions":[{"conditionType":"ScreenOpened","params":{"screenId":"LocationSelectingScreen"}},{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"b4550e0c-6337-4348-8c5a-f1ef880d88d8"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"4f7e4662-2488-49b2-806b-c14d3c8dc160","action":{"actionType":"Message","params":{"message":"TUTORIAL_GENDER_SELECTION","form":"full"}},"conditions":[{"conditionType":"ScreenOpened","params":{"screenId":"GenderSelectingScreen"}},{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"b4550e0c-6337-4348-8c5a-f1ef880d88d8"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"081349e0-502e-46d2-9afb-5f0bb4fe08a8","action":{"actionType":"Message","params":{"message":"TUTORIAL_TIMELINE_INTRO_1","form":"full"}},"conditions":[{"conditionType":"ScreenOpened","params":{"screenId":"TimelineScreen"}},{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"b4550e0c-6337-4348-8c5a-f1ef880d88d8"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"82e4ff6f-51d6-4fe8-b290-4340c61897bd","action":{"actionType":"AssignTask","params":{"tutorialTaskId":"fbce69a5-9fdf-4774-bc5e-538e33d96f04"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"081349e0-502e-46d2-9afb-5f0bb4fe08a8"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"25c09d24-c70f-4111-81de-4cf085d292a0","action":{"actionType":"Message","params":{"message":"TUTORIAL_TASKS_PANEL_1","form":"full"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"82e4ff6f-51d6-4fe8-b290-4340c61897bd"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"059f0491-4878-474e-b7da-aacd68463af2","action":{"actionType":"FocusOnGUIElement","params":{"guiElementId":"TasksPanel"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"25c09d24-c70f-4111-81de-4cf085d292a0"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"31cab579-84e1-4ae6-b05e-c4765b6fa72b","action":{"actionType":"RemoveFocus","params":{"guiElementId":"TasksPanel"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"059f0491-4878-474e-b7da-aacd68463af2"}}],"triggers":[{"triggerType":"TasksPanelMaximized","params":{}}]},{"id":"6eb02202-d75c-4c84-8e73-360f7e81f913","action":{"actionType":"Message","params":{"message":"TUTORIAL_TASKS_PANEL_2","form":"mini","vAligment":"top"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"31cab579-84e1-4ae6-b05e-c4765b6fa72b"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"670a8448-ac7f-450f-a0af-e3e631ecc041","action":{"actionType":"AssignTask","params":{"tutorialTaskId":"f1396740-bfeb-44e0-999f-7fe9b64ca665"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"6eb02202-d75c-4c84-8e73-360f7e81f913"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"6746775f-9291-4494-a24c-b7da5f758381","action":{"actionType":"IncTask","params":{"tutorialTaskId":"fbce69a5-9fdf-4774-bc5e-538e33d96f04"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"670a8448-ac7f-450f-a0af-e3e631ecc041"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"db047a14-2b03-4f17-a007-fddabde7646f","action":{"actionType":"Message","params":{"message":"TUTORIAL_TASKS_PANEL_4","vAligment":"top","form":"mini"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"6746775f-9291-4494-a24c-b7da5f758381"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"b19ce30e-20ec-4c0c-8d93-f71d9c67bf64","action":{"actionType":"FocusOnGUIElement","params":{"guiElementId":"TasksPanel"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"db047a14-2b03-4f17-a007-fddabde7646f"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"edac4597-9111-4af3-98db-2670a472af89","action":{"actionType":"RemoveFocus","params":{"guiElementId":"TasksPanel"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"b19ce30e-20ec-4c0c-8d93-f71d9c67bf64"}}],"triggers":[{"triggerType":"TasksPanelCollapsedFromMaximized","params":{}}]},{"id":"51c8d61e-16c1-4624-9fad-e1c2981f7500","action":{"actionType":"IncTask","params":{"tutorialTaskId":"f1396740-bfeb-44e0-999f-7fe9b64ca665"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"edac4597-9111-4af3-98db-2670a472af89"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"6f263a9a-fcb3-4952-a6ec-49d91e15097b","action":{"actionType":"Message","params":{"message":"TUTORIAL_TASKS_PANEL_5","form":"full"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"51c8d61e-16c1-4624-9fad-e1c2981f7500"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"33c14727-370c-43e0-9d34-590cfadf97c4","action":{"actionType":"Message","params":{"message":"TUTORIAL_HELLO_2","form":"full"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"112ee64a-671b-4670-9f0e-b1299f2e9350"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"4a8fbb43-2868-4fb6-8f54-1e0bba98f921","action":{"actionType":"Message","params":{"form":"mini","vAligment":"bottom","message":"TUTORIAL_TIMELINE_1"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"6f263a9a-fcb3-4952-a6ec-49d91e15097b"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"26913762-54b6-4bf5-9d77-033be5e140d6","action":{"actionType":"Message","params":{"message":"TUTORIAL_TIMELINE_2","form":"mini","vAligment":"bottom"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"4a8fbb43-2868-4fb6-8f54-1e0bba98f921"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"9d535ea8-ac44-4767-9fff-b4c64974ef92","action":{"actionType":"Message","params":{"message":"TUTORIAL_LIKE_1","form":"full"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"26913762-54b6-4bf5-9d77-033be5e140d6"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"db5560d2-844a-48ba-9613-e7d38663a011","action":{"actionType":"AssignTask","params":{"tutorialTaskId":"9888c94e-0adb-42a7-8073-e7c2b5333359"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"9d535ea8-ac44-4767-9fff-b4c64974ef92"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"b9a91efe-874a-46e5-9435-ddfed5306fda","action":{"actionType":"IncTask","params":{"tutorialTaskId":"9888c94e-0adb-42a7-8073-e7c2b5333359"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"db5560d2-844a-48ba-9613-e7d38663a011"}},{"conditionType":"TutorialTaskActive","params":{"tutorialTaskId":"9888c94e-0adb-42a7-8073-e7c2b5333359"}}],"triggers":[{"triggerType":"ContentLiked","params":{}}]},{"id":"2d265ded-b242-4fb4-8494-dabb407d9c16","action":{"actionType":"Message","params":{"message":"TUTORIAL_FOLLOW_1","form":"full"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"b9a91efe-874a-46e5-9435-ddfed5306fda"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"5eec58d4-d11e-4733-906d-dd45abad58f5","action":{"actionType":"AssignTask","params":{"tutorialTaskId":"c91da751-56d7-484a-9bbb-f6efe7807d68"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"2d265ded-b242-4fb4-8494-dabb407d9c16"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"442183ed-37f5-4e50-93e6-592d41028c6b","action":{"actionType":"FocusOnGUIElement","params":{"guiElementId":"FollowButton"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"5eec58d4-d11e-4733-906d-dd45abad58f5"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"a9240b3e-1e14-4dec-94f5-10b4eeb68dfd","action":{"actionType":"RemoveFocus","params":{"guiElementId":"FollowButton"}},"conditions":[{"conditionType":"TutorialTaskCompleted","params":{"tutorialTaskId":"c91da751-56d7-484a-9bbb-f6efe7807d68"}},{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"442183ed-37f5-4e50-93e6-592d41028c6b"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"a42e6128-ff93-49cd-acd4-32659df9045b","action":{"actionType":"Message","params":{"message":"TUTORIAL_FOLLOW_2","form":"full"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"a9240b3e-1e14-4dec-94f5-10b4eeb68dfd"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"6cbf6da0-4198-4181-8a3d-337c35233331","action":{"actionType":"Message","params":{"message":"TUTORIAL_RESOURCES_1","form":"full"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"83b3a501-c73a-4844-a395-c244bffc2c55"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"0dd6b97b-07f5-4b5b-9b8f-0ac81303f469","action":{"actionType":"Message","params":{"message":"TUTORIAL_RESOURCES_2","form":"full"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"87636259-ebe0-4bad-9dde-cf22b548046d"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"4923229a-7e34-4112-bbe7-04fbb436c361","action":{"actionType":"AssignTask","params":{"tutorialTaskId":"bdfed6bb-07ff-4256-aa2d-f73988b11d80"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"0dd6b97b-07f5-4b5b-9b8f-0ac81303f469"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"62d7725b-c2d5-443e-a029-6e662afcb69a","action":{"actionType":"Message","params":{"message":"TUTORIAL_RESOURCES_3","form":"full"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"4923229a-7e34-4112-bbe7-04fbb436c361"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"53a72a91-24de-4a19-b7f0-6210c35e3197","action":{"actionType":"IncTask","params":{"tutorialTaskId":"bdfed6bb-07ff-4256-aa2d-f73988b11d80"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"bfd8168a-242c-4047-b109-41a806c53612"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"83b3a501-c73a-4844-a395-c244bffc2c55","action":{"actionType":"FocusOnGUIElement","params":{"guiElementId":"ResourcesPanelLevelLabel"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"a42e6128-ff93-49cd-acd4-32659df9045b"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"b7d75a9c-3248-455b-b2e0-5b3a341a6996","action":{"actionType":"RemoveFocus","params":{"guiElementId":"ResourcesPanelLevelLabel"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"6cbf6da0-4198-4181-8a3d-337c35233331"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"87636259-ebe0-4bad-9dde-cf22b548046d","action":{"actionType":"FocusOnGUIElement","params":{"guiElementId":"ResourcesPanelXPLabel"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"b7d75a9c-3248-455b-b2e0-5b3a341a6996"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"bfd8168a-242c-4047-b109-41a806c53612","action":{"actionType":"RemoveFocus","params":{"guiElementId":"ResourcesPanelXPLabel"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"62d7725b-c2d5-443e-a029-6e662afcb69a"}}],"triggers":[{"triggerType":"Any","params":{}}]},{"id":"a96a9538-df1a-49dc-ae48-38915330fc3b","action":{"actionType":"Message","params":{"message":"TUTORIAL_SOLVE_QUEST_1","form":"mini","vAligment":"bottom"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"53a72a91-24de-4a19-b7f0-6210c35e3197"}}],"triggers":[{"triggerType":"ModalScreenOpened","params":{"screenId":"CompletedTasksReportScreen"}}]},{"id":"eaef1659-4b02-431c-b0e3-bfb57992b75f","action":{"actionType":"Message","params":{"message":"TUTORIAL_SOLVE_QUEST_2","form":"mini","vAligment":"bottom"}},"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"a96a9538-df1a-49dc-ae48-38915330fc3b"}}],"triggers":[{"triggerType":"Any","params":{}}]}]}