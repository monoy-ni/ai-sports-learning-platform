from app.agents.daily_analysis_agent import DailyAnalysisAgent
from app.agents.plan_generation_agent import PlanGenerationAgent
from app.agents.subagents.image_card_agent import ImageCardAgent
from app.agents.term_report_agent import TermReportAgent


class AgentRegistry:
    def __init__(self) -> None:
        image_card_agent = ImageCardAgent()
        self.daily_analysis = DailyAnalysisAgent(image_card_agent)
        self.plan_generation = PlanGenerationAgent()
        self.term_report = TermReportAgent()


agent_registry = AgentRegistry()

