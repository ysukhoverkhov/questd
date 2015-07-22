{"id":"iPhone","elements":[{"id":"112ee64a-671b-4670-9f0e-b1299f2e9350","actions":[{"actionType":"Message","params":{"form":"full","message":"TUTORIAL_HELLO_1"}},{"actionType":"Message","params":{"form":"full","message":"TUTORIAL_HELLO_2"}},{"actionType":"Message","params":{"form":"full","message":"TUTORIAL_EARLY_ACCESS_NOTE"}},{"actionType":"CloseTutorialElement","params":{"elementId":"112ee64a-671b-4670-9f0e-b1299f2e9350"}}],"serverActions":[],"conditions":[],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 01 - (01) Intro"}},{"id":"081349e0-502e-46d2-9afb-5f0bb4fe08a8","actions":[{"actionType":"Message","params":{"message":"TUTORIAL_TIMELINE_INTRO_1","form":"full"}},{"actionType":"CloseTutorialElement","params":{"elementId":"081349e0-502e-46d2-9afb-5f0bb4fe08a8"}}],"serverActions":[],"conditions":[{"conditionType":"ScreenOpened","params":{"screenId":"TimelineScreen"}},{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"112ee64a-671b-4670-9f0e-b1299f2e9350"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 01 - (01) Intro"}},{"id":"82e4ff6f-51d6-4fe8-b290-4340c61897bd","actions":[{"actionType":"Message","params":{"form":"full","message":"TUTORIAL_TASKS_PANEL_1"}},{"actionType":"AssignTutorialTask","params":{"tutorialTaskId":"fbce69a5-9fdf-4774-bc5e-538e33d96f04"}},{"actionType":"CloseTutorialElement","params":{"elementId":"82e4ff6f-51d6-4fe8-b290-4340c61897bd"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"081349e0-502e-46d2-9afb-5f0bb4fe08a8"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 01 - (10) Panel"}},{"id":"059f0491-4878-474e-b7da-aacd68463af2","actions":[{"actionType":"FocusOnGUIElement","params":{"guiElementId":"TasksPanel"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialTaskActive","params":{"tutorialTaskId":"fbce69a5-9fdf-4774-bc5e-538e33d96f04"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 01 - (10) Panel"}},{"id":"6746775f-9291-4494-a24c-b7da5f758381","actions":[{"actionType":"IncTutorialTask","params":{"tutorialTaskId":"fbce69a5-9fdf-4774-bc5e-538e33d96f04"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialTaskActive","params":{"tutorialTaskId":"fbce69a5-9fdf-4774-bc5e-538e33d96f04"}}],"triggers":[{"triggerType":"TasksPanelMaximized","params":{}}],"crud":{"sectionName":"Day 01 - (10) Panel"}},{"id":"31cab579-84e1-4ae6-b05e-c4765b6fa72b","actions":[{"actionType":"RemoveFocus","params":{"guiElementId":"TasksPanel"}},{"actionType":"CloseTutorialElement","params":{"elementId":"6746775f-9291-4494-a24c-b7da5f758381"}},{"actionType":"CloseTutorialElement","params":{"elementId":"059f0491-4878-474e-b7da-aacd68463af2"}},{"actionType":"CloseTutorialElement","params":{"elementId":"31cab579-84e1-4ae6-b05e-c4765b6fa72b"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialTaskCompleted","params":{"tutorialTaskId":"fbce69a5-9fdf-4774-bc5e-538e33d96f04"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 01 - (10) Panel"}},{"id":"6eb02202-d75c-4c84-8e73-360f7e81f913","actions":[{"actionType":"Message","params":{"message":"TUTORIAL_TASKS_PANEL_2","form":"mini","vAligment":"top"}},{"actionType":"AssignTutorialTask","params":{"tutorialTaskId":"f1396740-bfeb-44e0-999f-7fe9b64ca665"}},{"actionType":"CloseTutorialElement","params":{"elementId":"6eb02202-d75c-4c84-8e73-360f7e81f913"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"31cab579-84e1-4ae6-b05e-c4765b6fa72b"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 01 - (10) Panel"}},{"id":"b19ce30e-20ec-4c0c-8d93-f71d9c67bf64","actions":[{"actionType":"FocusOnGUIElement","params":{"guiElementId":"TasksPanel"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialTaskActive","params":{"tutorialTaskId":"f1396740-bfeb-44e0-999f-7fe9b64ca665"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 01 - (10) Panel"}},{"id":"51c8d61e-16c1-4624-9fad-e1c2981f7500","actions":[{"actionType":"IncTutorialTask","params":{"tutorialTaskId":"f1396740-bfeb-44e0-999f-7fe9b64ca665"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialTaskActive","params":{"tutorialTaskId":"f1396740-bfeb-44e0-999f-7fe9b64ca665"}}],"triggers":[{"triggerType":"TasksPanelCollapsedFromMaximized","params":{}}],"crud":{"sectionName":"Day 01 - (10) Panel"}},{"id":"edac4597-9111-4af3-98db-2670a472af89","actions":[{"actionType":"RemoveFocus","params":{"guiElementId":"TasksPanel"}},{"actionType":"CloseTutorialElement","params":{"elementId":"b19ce30e-20ec-4c0c-8d93-f71d9c67bf64"}},{"actionType":"CloseTutorialElement","params":{"elementId":"51c8d61e-16c1-4624-9fad-e1c2981f7500"}},{"actionType":"CloseTutorialElement","params":{"elementId":"edac4597-9111-4af3-98db-2670a472af89"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialTaskCompleted","params":{"tutorialTaskId":"f1396740-bfeb-44e0-999f-7fe9b64ca665"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 01 - (10) Panel"}},{"id":"db047a14-2b03-4f17-a007-fddabde7646f","actions":[{"actionType":"Message","params":{"message":"TUTORIAL_TASKS_PANEL_4","vAligment":"top","form":"mini"}},{"actionType":"Message","params":{"form":"full","message":"TUTORIAL_TASKS_PANEL_5"}},{"actionType":"CloseTutorialElement","params":{"elementId":"db047a14-2b03-4f17-a007-fddabde7646f"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"edac4597-9111-4af3-98db-2670a472af89"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 01 - (10) Panel"}},{"id":"4a8fbb43-2868-4fb6-8f54-1e0bba98f921","actions":[{"actionType":"Message","params":{"form":"mini","vAligment":"bottom","message":"TUTORIAL_TIMELINE_1"}},{"actionType":"Message","params":{"form":"mini","message":"TUTORIAL_TIMELINE_2","vAligment":"bottom"}},{"actionType":"CloseTutorialElement","params":{"elementId":"4a8fbb43-2868-4fb6-8f54-1e0bba98f921"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"db047a14-2b03-4f17-a007-fddabde7646f"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 01 - (20) Timeline"}},{"id":"9d535ea8-ac44-4767-9fff-b4c64974ef92","actions":[{"actionType":"Message","params":{"message":"TUTORIAL_LIKE_1","form":"mini","vAligment":"bottom"}},{"actionType":"AssignTutorialTask","params":{"tutorialTaskId":"9888c94e-0adb-42a7-8073-e7c2b5333359"}},{"actionType":"CloseTutorialElement","params":{"elementId":"9d535ea8-ac44-4767-9fff-b4c64974ef92"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"4a8fbb43-2868-4fb6-8f54-1e0bba98f921"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 01 - (20) Timeline"}},{"id":"5aca1dd3-a5f7-4e96-a45b-8c5960021a84","actions":[{"actionType":"FocusOnGUIElement","params":{"guiElementId":"QuestBlockLikeButton"}},{"actionType":"FocusOnGUIElement","params":{"guiElementId":"SolutionBlockLikeButton"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialTaskActive","params":{"tutorialTaskId":"9888c94e-0adb-42a7-8073-e7c2b5333359"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 01 - (20) Timeline"}},{"id":"b9a91efe-874a-46e5-9435-ddfed5306fda","actions":[{"actionType":"IncTutorialTask","params":{"tutorialTaskId":"9888c94e-0adb-42a7-8073-e7c2b5333359"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialTaskActive","params":{"tutorialTaskId":"9888c94e-0adb-42a7-8073-e7c2b5333359"}}],"triggers":[{"triggerType":"ContentLiked","params":{}}],"crud":{"sectionName":"Day 01 - (20) Timeline"}},{"id":"e61c1688-ec32-41b5-b6f5-a89e49db366a","actions":[{"actionType":"RemoveFocus","params":{"guiElementId":"QuestBlockLikeButton"}},{"actionType":"RemoveFocus","params":{"guiElementId":"SolutionBlockLikeButton"}},{"actionType":"CloseTutorialElement","params":{"elementId":"b9a91efe-874a-46e5-9435-ddfed5306fda"}},{"actionType":"CloseTutorialElement","params":{"elementId":"5aca1dd3-a5f7-4e96-a45b-8c5960021a84"}},{"actionType":"CloseTutorialElement","params":{"elementId":"e61c1688-ec32-41b5-b6f5-a89e49db366a"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialTaskCompleted","params":{"tutorialTaskId":"9888c94e-0adb-42a7-8073-e7c2b5333359"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 01 - (20) Timeline"}},{"id":"2d265ded-b242-4fb4-8494-dabb407d9c16","actions":[{"actionType":"Message","params":{"message":"TUTORIAL_FOLLOW_1","form":"full"}},{"actionType":"AssignTutorialTask","params":{"tutorialTaskId":"c91da751-56d7-484a-9bbb-f6efe7807d68"}},{"actionType":"CloseTutorialElement","params":{"elementId":"2d265ded-b242-4fb4-8494-dabb407d9c16"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"e61c1688-ec32-41b5-b6f5-a89e49db366a"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 01 - (20) Timeline"}},{"id":"442183ed-37f5-4e50-93e6-592d41028c6b","actions":[{"actionType":"FocusOnGUIElement","params":{"guiElementId":"FollowButton"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialTaskActive","params":{"tutorialTaskId":"c91da751-56d7-484a-9bbb-f6efe7807d68"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 01 - (20) Timeline"}},{"id":"a9240b3e-1e14-4dec-94f5-10b4eeb68dfd","actions":[{"actionType":"RemoveFocus","params":{"guiElementId":"FollowButton"}},{"actionType":"CloseTutorialElement","params":{"elementId":"442183ed-37f5-4e50-93e6-592d41028c6b"}},{"actionType":"CloseTutorialElement","params":{"elementId":"a9240b3e-1e14-4dec-94f5-10b4eeb68dfd"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialTaskCompleted","params":{"tutorialTaskId":"c91da751-56d7-484a-9bbb-f6efe7807d68"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 01 - (20) Timeline"}},{"id":"a42e6128-ff93-49cd-acd4-32659df9045b","actions":[{"actionType":"Message","params":{"message":"TUTORIAL_FOLLOW_2","form":"full"}},{"actionType":"CloseTutorialElement","params":{"elementId":"a42e6128-ff93-49cd-acd4-32659df9045b"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"a9240b3e-1e14-4dec-94f5-10b4eeb68dfd"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 01 - (20) Timeline"}},{"id":"855af4c5-6d6e-4cff-a5ca-7f9347097f0a","actions":[{"actionType":"AssignTutorialTask","params":{"tutorialTaskId":"bdfed6bb-07ff-4256-aa2d-f73988b11d80"}},{"actionType":"IncTutorialTask","params":{"tutorialTaskId":"bdfed6bb-07ff-4256-aa2d-f73988b11d80"}},{"actionType":"CloseTutorialElement","params":{"elementId":"855af4c5-6d6e-4cff-a5ca-7f9347097f0a"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"a42e6128-ff93-49cd-acd4-32659df9045b"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 01 - (30) Solve Quest"}},{"id":"d8c8a578-d50f-4f07-9b24-b851b3b05ba5","actions":[{"actionType":"Message","params":{"form":"mini","vAligment":"bottom","message":"TUTORIAL_NEW_LEVEL_1"}},{"actionType":"CloseTutorialElement","params":{"elementId":"d8c8a578-d50f-4f07-9b24-b851b3b05ba5"}}],"serverActions":[],"conditions":[{"conditionType":"ProfileVariableState","params":{"profileFieldPath":"publicProfile.level","predicate":">= 2"}},{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"855af4c5-6d6e-4cff-a5ca-7f9347097f0a"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 01 - (30) Solve Quest"}},{"id":"83b3a501-c73a-4844-a395-c244bffc2c55","actions":[{"actionType":"FocusOnGUIElement","params":{"guiElementId":"ResourcesPanelLevelLabel"}},{"actionType":"Message","params":{"form":"full","message":"TUTORIAL_RESOURCES_1"}},{"actionType":"RemoveFocus","params":{"guiElementId":"ResourcesPanelLevelLabel"}},{"actionType":"CloseTutorialElement","params":{"elementId":"83b3a501-c73a-4844-a395-c244bffc2c55"}}],"serverActions":[],"conditions":[{"conditionType":"ScreenOpened","params":{"screenId":"TimelineScreen"}},{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"d8c8a578-d50f-4f07-9b24-b851b3b05ba5"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 01 - (30) Solve Quest"}},{"id":"87636259-ebe0-4bad-9dde-cf22b548046d","actions":[{"actionType":"FocusOnGUIElement","params":{"guiElementId":"ResourcesPanelXPLabel"}},{"actionType":"Message","params":{"form":"full","message":"TUTORIAL_RESOURCES_2"}},{"actionType":"RemoveFocus","params":{"guiElementId":"ResourcesPanelXPLabel"}},{"actionType":"CloseTutorialElement","params":{"elementId":"87636259-ebe0-4bad-9dde-cf22b548046d"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"83b3a501-c73a-4844-a395-c244bffc2c55"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 01 - (30) Solve Quest"}},{"id":"874bce95-bd59-4211-b2d7-148650755e01","actions":[{"actionType":"FocusOnGUIElement","params":{"guiElementId":"ResourcesPanelQbitsLabel"}},{"actionType":"Message","params":{"form":"full","message":"TUTORIAL_RESOURCES_3"}},{"actionType":"RemoveFocus","params":{"guiElementId":"ResourcesPanelQbitsLabel"}},{"actionType":"CloseTutorialElement","params":{"elementId":"874bce95-bd59-4211-b2d7-148650755e01"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"87636259-ebe0-4bad-9dde-cf22b548046d"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 01 - (30) Solve Quest"}},{"id":"fe3d81d9-2552-497e-858c-2761775558c0","actions":[{"actionType":"Message","params":{"form":"full","message":"TUTORIAL_SOLVE_QUEST_1"}},{"actionType":"Dummy","params":{"tutorialQuestId":"tutorial_1"}},{"actionType":"AssignTutorialTask","params":{"tutorialTaskId":"6e7606ce-4305-4a47-af4b-885ac1ee7f42"}},{"actionType":"CloseTutorialElement","params":{"elementId":"fe3d81d9-2552-497e-858c-2761775558c0"}}],"serverActions":[],"conditions":[{"conditionType":"ScreenOpened","params":{"screenId":"TimelineScreen"}},{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"874bce95-bd59-4211-b2d7-148650755e01"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 01 - (30) Solve Quest"}},{"id":"519925d5-54ca-49da-ae7c-527f481e88c0","actions":[{"actionType":"FocusOnGUIElement","params":{"guiElementId":"SolveQuestButton"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialTaskActive","params":{"tutorialTaskId":"6e7606ce-4305-4a47-af4b-885ac1ee7f42"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 01 - (30) Solve Quest"}},{"id":"a1a34ab0-297a-4b98-854c-9f951382cb8c","actions":[{"actionType":"Message","params":{"form":"full","message":"TUTORIAL_SOLVE_QUEST_2"}},{"actionType":"CloseTutorialElement","params":{"elementId":"a1a34ab0-297a-4b98-854c-9f951382cb8c"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialTaskActive","params":{"tutorialTaskId":"6e7606ce-4305-4a47-af4b-885ac1ee7f42"}}],"triggers":[{"triggerType":"ScreenOpened","params":{"screenId":"SolutionContentSelectingScreen"}}],"crud":{"sectionName":"Day 01 - (30) Solve Quest"}},{"id":"fe61b533-eba4-49c3-95b6-63e1887401c6","actions":[{"actionType":"RemoveFocus","params":{"guiElementId":"SolveQuestButton"}},{"actionType":"CloseTutorialElement","params":{"elementId":"519925d5-54ca-49da-ae7c-527f481e88c0"}},{"actionType":"CloseTutorialElement","params":{"elementId":"fe61b533-eba4-49c3-95b6-63e1887401c6"}}],"serverActions":[],"conditions":[{"conditionType":"ScreenOpened","params":{"screenId":"TimelineScreen"}},{"conditionType":"TutorialTaskCompleted","params":{"tutorialTaskId":"6e7606ce-4305-4a47-af4b-885ac1ee7f42"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 01 - (30) Solve Quest"}},{"id":"7ccd7716-177c-428b-957b-ccd69ccf0f39","actions":[{"actionType":"Message","params":{"form":"full","message":"TUTORIAL_SOLVE_QUEST_3"}},{"actionType":"CloseTutorialElement","params":{"elementId":"7ccd7716-177c-428b-957b-ccd69ccf0f39"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"fe61b533-eba4-49c3-95b6-63e1887401c6"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 01 - (30) Solve Quest"}},{"id":"1ab0785b-e137-4545-9b8f-256629f57e74","actions":[{"actionType":"Message","params":{"message":"TUTORIAL_END_DAY_1","form":"full"}},{"actionType":"AssignTutorialTask","params":{"tutorialTaskId":"29040247-3e8a-4ada-bc55-3f619626e713"}},{"actionType":"AssignTutorialTask","params":{"tutorialTaskId":"beb8cf1e-de99-4f7a-a0bc-ca3ca552d1bb"}},{"actionType":"SetReminder","params":{"message":"TUTORIAL_REMINDER_DAY_1","delay":"480"}},{"actionType":"CloseTutorialElement","params":{"elementId":"1ab0785b-e137-4545-9b8f-256629f57e74"}}],"serverActions":[{"actionType":"AssignDailyTasks"}],"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"7ccd7716-177c-428b-957b-ccd69ccf0f39"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 01 - (50) Outtro"}},{"id":"b5241af0-b5fb-40a6-82f2-fc4a3f09327a","actions":[{"actionType":"IncTutorialTask","params":{"tutorialTaskId":"29040247-3e8a-4ada-bc55-3f619626e713"}},{"actionType":"CloseTutorialElement","params":{"elementId":"b5241af0-b5fb-40a6-82f2-fc4a3f09327a"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialTaskActive","params":{"tutorialTaskId":"29040247-3e8a-4ada-bc55-3f619626e713"}}],"triggers":[{"triggerType":"SidePanelOpened","params":{}}],"crud":{"sectionName":"Day 01 - (50) Outtro"}},{"id":"3ad82fa2-d806-4925-9d2e-a821d0111f2b","actions":[{"actionType":"IncTutorialTask","params":{"tutorialTaskId":"beb8cf1e-de99-4f7a-a0bc-ca3ca552d1bb"}},{"actionType":"CloseTutorialElement","params":{"elementId":"3ad82fa2-d806-4925-9d2e-a821d0111f2b"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialTaskActive","params":{"tutorialTaskId":"beb8cf1e-de99-4f7a-a0bc-ca3ca552d1bb"}}],"triggers":[{"triggerType":"ScreenOpened","params":{"screenId":"OwnProfileScreen"}}],"crud":{"sectionName":"Day 01 - (50) Outtro"}},{"id":"8d9c8115-3062-4f4e-b967-0873876088de","actions":[{"actionType":"Message","params":{"message":"TUTORIAL_CHALLENGES_1","form":"full"}},{"actionType":"Message","params":{"form":"full","message":"TUTORIAL_CHALLENGES_11"}},{"actionType":"AssignTutorialTask","params":{"tutorialTaskId":"ef84c907-8056-4175-b7ba-1537881707c7"}},{"actionType":"CloseTutorialElement","params":{"elementId":"8d9c8115-3062-4f4e-b967-0873876088de"}}],"serverActions":[],"conditions":[{"conditionType":"ProfileVariableState","params":{"predicate":">= 28800","profileFieldPath":"analytics.secondsFromProfileCreation"}},{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"1ab0785b-e137-4545-9b8f-256629f57e74"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 02 - (20) Challenges"}},{"id":"aa9b26b7-7ef7-4763-a1b6-95b4cd95cfe4","actions":[{"actionType":"FocusOnGUIElement","params":{"guiElementId":"SolveQuestButton"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialTaskActive","params":{"tutorialTaskId":"ef84c907-8056-4175-b7ba-1537881707c7"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 02 - (20) Challenges"}},{"id":"c48472c5-5f53-4406-9be7-bf6fac007c9d","actions":[{"actionType":"RemoveFocus","params":{"guiElementId":"SolveQuestButton"}},{"actionType":"CloseTutorialElement","params":{"elementId":"aa9b26b7-7ef7-4763-a1b6-95b4cd95cfe4"}},{"actionType":"CloseTutorialElement","params":{"elementId":"c48472c5-5f53-4406-9be7-bf6fac007c9d"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialTaskCompleted","params":{"tutorialTaskId":"ef84c907-8056-4175-b7ba-1537881707c7"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 02 - (20) Challenges"}},{"id":"a3afbdd0-0fd0-4b2f-89b5-28d927f95a80","actions":[{"actionType":"Message","params":{"message":"TUTORIAL_CHALLENGES_2","form":"mini","vAligment":"bottom"}},{"actionType":"Message","params":{"form":"full","message":"TUTORIAL_CHALLENGES_3"}},{"actionType":"AssignTutorialTask","params":{"tutorialTaskId":"eaf25ea8-d91b-431d-a4ed-2f78c9678f64"}},{"actionType":"CloseTutorialElement","params":{"elementId":"a3afbdd0-0fd0-4b2f-89b5-28d927f95a80"}}],"serverActions":[],"conditions":[{"conditionType":"ScreenOpened","params":{"screenId":"TimelineScreen"}},{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"c48472c5-5f53-4406-9be7-bf6fac007c9d"}},{"conditionType":"ProfileVariableState","params":{"profileFieldPath":"publicProfile.level","predicate":">= 3"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 02 - (20) Challenges"}},{"id":"756d683c-9dd0-4ae3-8899-f4450ff07835","actions":[{"actionType":"FocusOnGUIElement","params":{"guiElementId":"QuestBlockMoreButton"}},{"actionType":"FocusOnGUIElement","params":{"guiElementId":"QuestBlockSolutionsButton"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialTaskActive","params":{"tutorialTaskId":"eaf25ea8-d91b-431d-a4ed-2f78c9678f64"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 02 - (20) Challenges"}},{"id":"6bf7eb80-9276-417b-ba32-c1c747f37ae2","actions":[{"actionType":"IncTutorialTask","params":{"tutorialTaskId":"eaf25ea8-d91b-431d-a4ed-2f78c9678f64"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialTaskActive","params":{"tutorialTaskId":"eaf25ea8-d91b-431d-a4ed-2f78c9678f64"}}],"triggers":[{"triggerType":"ScreenOpened","params":{"screenId":"SolutionsListScreen"}}],"crud":{"sectionName":"Day 02 - (20) Challenges"}},{"id":"a13d3fe3-e6dd-45c7-b32c-eb7dab4fd4eb","actions":[{"actionType":"RemoveFocus","params":{"guiElementId":"QuestBlockMoreButton"}},{"actionType":"RemoveFocus","params":{"guiElementId":"QuestBlockSolutionsButton"}},{"actionType":"CloseTutorialElement","params":{"elementId":"756d683c-9dd0-4ae3-8899-f4450ff07835"}},{"actionType":"CloseTutorialElement","params":{"elementId":"6bf7eb80-9276-417b-ba32-c1c747f37ae2"}},{"actionType":"CloseTutorialElement","params":{"elementId":"a13d3fe3-e6dd-45c7-b32c-eb7dab4fd4eb"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialTaskCompleted","params":{"tutorialTaskId":"eaf25ea8-d91b-431d-a4ed-2f78c9678f64"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 02 - (20) Challenges"}},{"id":"01fb496f-e82a-4c1d-a907-ea5b9819e249","actions":[{"actionType":"Message","params":{"form":"full","message":"TUTORIAL_CHALLENGES_4"}},{"actionType":"AssignTutorialTask","params":{"tutorialTaskId":"74505643-d1ef-450d-b781-578c08c65b51"}},{"actionType":"CloseTutorialElement","params":{"elementId":"01fb496f-e82a-4c1d-a907-ea5b9819e249"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"a13d3fe3-e6dd-45c7-b32c-eb7dab4fd4eb"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 02 - (20) Challenges"}},{"id":"0cd18d13-c4d6-444b-b66f-354db8a0aec7","actions":[{"actionType":"FocusOnGUIElement","params":{"guiElementId":"ChallengeBattleButton"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialTaskActive","params":{"tutorialTaskId":"74505643-d1ef-450d-b781-578c08c65b51"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 02 - (20) Challenges"}},{"id":"d55b0d10-6707-442c-9a3b-7e827f274866","actions":[{"actionType":"RemoveFocus","params":{"guiElementId":"ChallengeBattleButton"}},{"actionType":"CloseTutorialElement","params":{"elementId":"0cd18d13-c4d6-444b-b66f-354db8a0aec7"}},{"actionType":"CloseTutorialElement","params":{"elementId":"d55b0d10-6707-442c-9a3b-7e827f274866"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialTaskCompleted","params":{"tutorialTaskId":"74505643-d1ef-450d-b781-578c08c65b51"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 02 - (20) Challenges"}},{"id":"2efdbb55-4632-44e7-ba60-be399a129edf","actions":[{"actionType":"Message","params":{"form":"full","message":"TUTORIAL_CHALLENGES_5"}},{"actionType":"CloseTutorialElement","params":{"elementId":"2efdbb55-4632-44e7-ba60-be399a129edf"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"d55b0d10-6707-442c-9a3b-7e827f274866"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 02 - (20) Challenges"}},{"id":"1fb7780f-4a1b-4756-b421-0114cf796c0d","actions":[{"actionType":"Message","params":{"message":"TUTORIAL_END_DAY_2","form":"full"}},{"actionType":"SetReminder","params":{"delay":"480","message":"TUTORIAL_REMINDER_DAY_2"}},{"actionType":"CloseTutorialElement","params":{"elementId":"1fb7780f-4a1b-4756-b421-0114cf796c0d"}}],"serverActions":[{"actionType":"AssignDailyTasks"}],"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"2efdbb55-4632-44e7-ba60-be399a129edf"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 02 - (50) Outtro"}},{"id":"e22d5da5-cc77-4934-b7cb-4ba5ed561654","actions":[{"actionType":"Message","params":{"form":"full","message":"TUTORIAL_COMMENTS_1"}},{"actionType":"AssignTutorialTask","params":{"tutorialTaskId":"44b53224-433a-4e9d-b669-fd275023ac81"}},{"actionType":"CloseTutorialElement","params":{"elementId":"e22d5da5-cc77-4934-b7cb-4ba5ed561654"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"1fb7780f-4a1b-4756-b421-0114cf796c0d"}},{"conditionType":"ProfileVariableState","params":{"predicate":">= 86400","profileFieldPath":"analytics.secondsFromProfileCreation"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 03 - (30) Comments"}},{"id":"2c63f025-52aa-44f5-999d-1a550e4e080b","actions":[{"actionType":"FocusOnGUIElement","params":{"guiElementId":"QuestBlockCommentsButton"}},{"actionType":"FocusOnGUIElement","params":{"guiElementId":"SolutionBlockCommentsButton"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialTaskActive","params":{"tutorialTaskId":"44b53224-433a-4e9d-b669-fd275023ac81"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 03 - (30) Comments"}},{"id":"faf3cefd-aea3-4747-a114-986a0ae474d5","actions":[{"actionType":"IncTutorialTask","params":{"tutorialTaskId":"44b53224-433a-4e9d-b669-fd275023ac81"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialTaskActive","params":{"tutorialTaskId":"44b53224-433a-4e9d-b669-fd275023ac81"}}],"triggers":[{"triggerType":"ScreenOpened","params":{"screenId":"ContentCommentsScreen"}}],"crud":{"sectionName":"Day 03 - (30) Comments"}},{"id":"6762c529-6b6e-49d5-a570-c0e395ff9c72","actions":[{"actionType":"RemoveFocus","params":{"guiElementId":"QuestBlockCommentsButton"}},{"actionType":"RemoveFocus","params":{"guiElementId":"SolutionBlockCommentsButton"}},{"actionType":"CloseTutorialElement","params":{"elementId":"2c63f025-52aa-44f5-999d-1a550e4e080b"}},{"actionType":"CloseTutorialElement","params":{"elementId":"faf3cefd-aea3-4747-a114-986a0ae474d5"}},{"actionType":"CloseTutorialElement","params":{"elementId":"6762c529-6b6e-49d5-a570-c0e395ff9c72"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialTaskCompleted","params":{"tutorialTaskId":"44b53224-433a-4e9d-b669-fd275023ac81"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 03 - (30) Comments"}},{"id":"0b023981-8766-40a1-a420-ec6fd88541c2","actions":[{"actionType":"Message","params":{"form":"full","message":"TUTORIAL_COMMENTS_2"}},{"actionType":"CloseTutorialElement","params":{"elementId":"0b023981-8766-40a1-a420-ec6fd88541c2"}}],"serverActions":[{"actionType":"AssignDailyTasks"},{"actionType":"RemoveDailyTasksSuppression"}],"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"6762c529-6b6e-49d5-a570-c0e395ff9c72"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 03 - (30) Comments"}},{"id":"e7174d06-cd3d-4e3e-8ced-6f8608a17d20","actions":[{"actionType":"Message","params":{"form":"full","message":"TUTORIAL_FEEDBACK_1"}},{"actionType":"AssignTutorialTask","params":{"tutorialTaskId":"63bf170b-232d-41ad-91b9-17465e4c530e"}},{"actionType":"CloseTutorialElement","params":{"elementId":"e7174d06-cd3d-4e3e-8ced-6f8608a17d20"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"0b023981-8766-40a1-a420-ec6fd88541c2"}},{"conditionType":"ProfileVariableState","params":{"predicate":">= 86400","profileFieldPath":"analytics.secondsFromProfileCreation"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 04 - (50) Feedback"}},{"id":"41e31be4-b2a5-4429-8636-c85026ea86cb","actions":[{"actionType":"FocusOnGUIElement","params":{"guiElementId":"SideMenuButton"}},{"actionType":"Dummy","params":{}}],"serverActions":[],"conditions":[{"conditionType":"TutorialTaskActive","params":{"tutorialTaskId":"63bf170b-232d-41ad-91b9-17465e4c530e"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 04 - (50) Feedback"}},{"id":"0956c697-b062-4b30-aaf4-a1e6617543a2","actions":[{"actionType":"IncTutorialTask","params":{"tutorialTaskId":"63bf170b-232d-41ad-91b9-17465e4c530e"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialTaskActive","params":{"tutorialTaskId":"63bf170b-232d-41ad-91b9-17465e4c530e"}}],"triggers":[{"triggerType":"FeedbackSent","params":{}}],"crud":{"sectionName":"Day 04 - (50) Feedback"}},{"id":"4e8fe81d-418d-4592-b578-e805ddf62b0f","actions":[{"actionType":"RemoveFocus","params":{"guiElementId":"SideMenuButton"}},{"actionType":"Dummy","params":{}},{"actionType":"CloseTutorialElement","params":{"elementId":"41e31be4-b2a5-4429-8636-c85026ea86cb"}},{"actionType":"CloseTutorialElement","params":{"elementId":"0956c697-b062-4b30-aaf4-a1e6617543a2"}},{"actionType":"CloseTutorialElement","params":{"elementId":"4e8fe81d-418d-4592-b578-e805ddf62b0f"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialTaskCompleted","params":{"tutorialTaskId":"63bf170b-232d-41ad-91b9-17465e4c530e"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Day 04 - (50) Feedback"}},{"id":"8865bf55-b6f7-4b2f-a820-dc4d1705e308","actions":[{"actionType":"Dummy","params":{}}],"serverActions":[],"conditions":[{"conditionType":"Dummy","params":{}}],"triggers":[{"triggerType":"Dummy","params":{}}],"crud":{"sectionName":"Empty"}},{"id":"5042cb72-0081-47a4-ab53-75596344ac16","actions":[{"actionType":"FocusOnGUIElement","params":{"guiElementId":"BattleBlock"}},{"actionType":"ScrollToNearestBattleInTimeLine","params":{}},{"actionType":"Message","params":{"form":"mini","message":"TUTORIAL_BATTLES_2","vAligment":"bottom"}},{"actionType":"Message","params":{"form":"full","message":"TUTORIAL_BATTLES_3"}},{"actionType":"Message","params":{"form":"full","message":"TUTORIAL_BATTLES_4"}},{"actionType":"Message","params":{"form":"full","message":"TUTORIAL_BATTLES_5"}},{"actionType":"RemoveFocus","params":{"guiElementId":"BattleBlock"}},{"actionType":"CloseTutorialElement","params":{"elementId":"5042cb72-0081-47a4-ab53-75596344ac16"}}],"serverActions":[],"conditions":[{"conditionType":"Dummy","params":{"screenId":"TimelineScreen"}}],"triggers":[{"triggerType":"OurBattleIsShown","params":{}}],"crud":{"sectionName":"Feature - (100) Duels"}},{"id":"b1e41e1e-04d0-49e7-a3f6-83593aa13921","actions":[{"actionType":"Message","params":{"form":"full","message":"TUTORIAL_REPORT_1"}},{"actionType":"AssignTutorialTask","params":{"tutorialTaskId":"f8e7bb10-621b-4a12-a397-e4fc2f794aa7"}},{"actionType":"CloseTutorialElement","params":{"elementId":"b1e41e1e-04d0-49e7-a3f6-83593aa13921"}}],"serverActions":[],"conditions":[{"conditionType":"ScreenOpened","params":{"screenId":"TimelineScreen"}},{"conditionType":"ProfileVariableState","params":{"profileFieldPath":"rights.unlockedFunctionalityStrings","predicate":"ArrayContains Report"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Feature - (110) Reports"}},{"id":"5fe382b2-936d-45ac-9f61-0e6e75b078ee","actions":[{"actionType":"FocusOnGUIElement","params":{"guiElementId":"ContentReportButton"}},{"actionType":"FocusOnGUIElement","params":{"guiElementId":"QuestBlockMoreButton"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialTaskActive","params":{"tutorialTaskId":"f8e7bb10-621b-4a12-a397-e4fc2f794aa7"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Feature - (110) Reports"}},{"id":"ede42305-41c6-4a3d-9b56-59a5d022614a","actions":[{"actionType":"IncTutorialTask","params":{"tutorialTaskId":"f8e7bb10-621b-4a12-a397-e4fc2f794aa7"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialTaskActive","params":{"tutorialTaskId":"f8e7bb10-621b-4a12-a397-e4fc2f794aa7"}}],"triggers":[{"triggerType":"ButtonPressed","params":{"guiElementId":"ContentReportButton"}}],"crud":{"sectionName":"Feature - (110) Reports"}},{"id":"363ee7ea-4db3-4dbf-a10d-c85e9359bbc4","actions":[{"actionType":"Message","params":{"message":"TUTORIAL_REPORT_2","form":"full"}},{"actionType":"RemoveFocus","params":{"guiElementId":"ContentReportButton"}},{"actionType":"RemoveFocus","params":{"guiElementId":"QuestBlockMoreButton"}},{"actionType":"CloseTutorialElement","params":{"elementId":"5fe382b2-936d-45ac-9f61-0e6e75b078ee"}},{"actionType":"CloseTutorialElement","params":{"elementId":"ede42305-41c6-4a3d-9b56-59a5d022614a"}},{"actionType":"CloseTutorialElement","params":{"elementId":"363ee7ea-4db3-4dbf-a10d-c85e9359bbc4"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialTaskCompleted","params":{"tutorialTaskId":"f8e7bb10-621b-4a12-a397-e4fc2f794aa7"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Feature - (110) Reports"}},{"id":"7c5ac2c1-fcd5-428f-8d4f-d22ddd6607e8","actions":[{"actionType":"Message","params":{"message":"TUTORIAL_COMMENTS_5","form":"full"}},{"actionType":"AssignTutorialTask","params":{"tutorialTaskId":"78814d2d-da3e-4435-987e-1a437b8d2b8d"}},{"actionType":"CloseTutorialElement","params":{"elementId":"7c5ac2c1-fcd5-428f-8d4f-d22ddd6607e8"}}],"serverActions":[],"conditions":[{"conditionType":"ScreenOpened","params":{"screenId":"TimelineScreen"}},{"conditionType":"TutorialElementClosed","params":{"tutorialElementId":"363ee7ea-4db3-4dbf-a10d-c85e9359bbc4"}},{"conditionType":"ProfileVariableState","params":{"profileFieldPath":"rights.unlockedFunctionalityStrings","predicate":"ArrayContains PostComments"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Feature - (120) Comments"}},{"id":"bbf86e54-9abb-42c3-87f8-21e8a7ed7bfd","actions":[{"actionType":"FocusOnGUIElement","params":{"guiElementId":"QuestBlockCommentsButton"}},{"actionType":"FocusOnGUIElement","params":{"guiElementId":"SolutionBlockCommentsButton"}},{"actionType":"FocusOnGUIElement","params":{"guiElementId":"SendCommentButton"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialTaskActive","params":{"tutorialTaskId":"78814d2d-da3e-4435-987e-1a437b8d2b8d"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Feature - (120) Comments"}},{"id":"48411d9a-b1f7-4360-bd88-af69bbd1b13c","actions":[{"actionType":"IncTutorialTask","params":{"tutorialTaskId":"78814d2d-da3e-4435-987e-1a437b8d2b8d"}}],"serverActions":[],"conditions":[{"conditionType":"Dummy","params":{"tutorialTaskId":"78814d2d-da3e-4435-987e-1a437b8d2b8d"}}],"triggers":[{"triggerType":"Dummy","params":{"guiElementId":"SEND","COMMENT":"DELETE_ME"}}],"crud":{"sectionName":"Feature - (120) Comments"}},{"id":"6db81b50-6f54-415f-b4d1-fc4c3fa810b4","actions":[{"actionType":"RemoveFocus","params":{"guiElementId":"QuestBlockCommentsButton"}},{"actionType":"RemoveFocus","params":{"guiElementId":"SolutionBlockCommentsButton"}},{"actionType":"RemoveFocus","params":{"guiElementId":"SendCommentButton"}},{"actionType":"Message","params":{"form":"full","message":"TUTORIAL_COMMENTS_6"}},{"actionType":"CloseTutorialElement","params":{"elementId":"bbf86e54-9abb-42c3-87f8-21e8a7ed7bfd"}},{"actionType":"Dummy","params":{"elementId":"48411d9a-b1f7-4360-bd88-af69bbd1b13c"}},{"actionType":"CloseTutorialElement","params":{"elementId":"6db81b50-6f54-415f-b4d1-fc4c3fa810b4"}}],"serverActions":[],"conditions":[{"conditionType":"TutorialTaskCompleted","params":{"tutorialTaskId":"78814d2d-da3e-4435-987e-1a437b8d2b8d"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Feature - (120) Comments"}},{"id":"b747ec32-7688-4095-94b7-2884d3531f4b","actions":[{"actionType":"Message","params":{"message":"TUTORIAL_LOCATION_SELECTION","form":"full"}},{"actionType":"CloseTutorialElement","params":{"elementId":"b747ec32-7688-4095-94b7-2884d3531f4b"}}],"serverActions":[],"conditions":[{"conditionType":"ScreenOpened","params":{"screenId":"LocationSelectingScreen"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Screen - (010) Location Selection"}},{"id":"4f7e4662-2488-49b2-806b-c14d3c8dc160","actions":[{"actionType":"Message","params":{"message":"TUTORIAL_GENDER_SELECTION","form":"full"}},{"actionType":"CloseTutorialElement","params":{"elementId":"4f7e4662-2488-49b2-806b-c14d3c8dc160"}}],"serverActions":[],"conditions":[{"conditionType":"ScreenOpened","params":{"screenId":"GenderSelectingScreen"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Screen - (015) Gender Selection"}},{"id":"b8f6d254-f998-4d98-96d7-ed57dc7e9dc3","actions":[{"actionType":"Message","params":{"message":"TUTORIAL_DUEL_RESULT_1","form":"full"}},{"actionType":"CloseTutorialElement","params":{"elementId":"b8f6d254-f998-4d98-96d7-ed57dc7e9dc3"}}],"serverActions":[],"conditions":[{"conditionType":"ScreenOpened","params":{"screenId":"SolutionResultScreen"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Screen - (220) Duel Result"}},{"id":"ec89c66a-54d2-4ea3-817a-3476717d45c2","actions":[{"actionType":"Message","params":{"form":"full","message":"TUTORIAL_RESULTS_SUMMARY_1"}},{"actionType":"CloseTutorialElement","params":{"elementId":"ec89c66a-54d2-4ea3-817a-3476717d45c2"}}],"serverActions":[],"conditions":[{"conditionType":"ScreenOpened","params":{"screenId":"DailyResultsScreen"}}],"triggers":[{"triggerType":"Any","params":{}}],"crud":{"sectionName":"Screen - (250) Summary"}}]}