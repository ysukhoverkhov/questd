package controllers.domain.app.quest

import controllers.domain._
import org.mockito.Matchers
import testhelpers.domainstubs._

class QuestAPISpecs extends BaseAPISpecs {

  "Quest API" should {

    "Decease quest points if it was selected to time line" in context {

      val q = createQuestStub()

//      val spiedQuestSolutionLogic = spy(new QuestSolutionLogic(sol, api.api))
//      when(api.questSolution2Logic(sol)).thenReturn(spiedQuestSolutionLogic)

      quest.updatePoints(Matchers.eq(q.id), Matchers.eq(-1), any, any, any, any) returns Some(q)

      val result = api.selectQuestToTimeLine(SelectQuestToTimeLineRequest(q))

      result must beEqualTo(OkApiResult(SelectQuestToTimeLineResult()))

      there was one(quest).updatePoints(Matchers.eq(q.id), Matchers.eq(-1), any, any, any, any)
    }
  }
}


