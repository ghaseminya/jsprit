package jsprit.core.problem.constraint;

import jsprit.core.algorithm.state.StateManager;
import jsprit.core.problem.VehicleRoutingProblem;
import jsprit.core.problem.job.Service;
import jsprit.core.problem.misc.JobInsertionContext;
import jsprit.core.problem.solution.route.VehicleRoute;
import jsprit.core.problem.vehicle.VehicleImpl;
import jsprit.core.problem.vehicle.VehicleType;
import jsprit.core.problem.vehicle.VehicleTypeImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class SkillConstraintTest {

    private HardRouteStateLevelConstraint skillConstraint;

    private VehicleRoute route;

    private VehicleImpl vehicle;

    private VehicleImpl vehicle2;

    private VehicleRoutingProblem vrp;

    @Before
    public void doBefore(){
        VehicleType type = VehicleTypeImpl.Builder.newInstance("t").build();
        vehicle = VehicleImpl.Builder.newInstance("v").addSkill("skill1").addSkill("skill2").addSkill("skill3").addSkill("skill4").setStartLocationId("start").setType(type).build();
        vehicle2 = VehicleImpl.Builder.newInstance("v").addSkill("skill4").addSkill("skill5").setStartLocationId("start").setType(type).build();

        Service service = Service.Builder.newInstance("s").setLocationId("loc").addRequiredSkill("skill1").build();
        Service service2 = Service.Builder.newInstance("s2").setLocationId("loc").addRequiredSkill("skill1").addRequiredSkill("skill2").addRequiredSkill("skill3").build();

        Service service3 = Service.Builder.newInstance("s3").setLocationId("loc").addRequiredSkill("skill4").addRequiredSkill("skill5").build();
        Service service4 = Service.Builder.newInstance("s4").setLocationId("loc").addRequiredSkill("skill1").build();

        vrp = VehicleRoutingProblem.Builder.newInstance().addVehicle(vehicle).addVehicle(vehicle2).addJob(service)
                .addJob(service2).addJob(service3).addJob(service4).build();

        route = VehicleRoute.Builder.newInstance(vehicle).setJobActivityFactory(vrp.getJobActivityFactory()).addService(service).addService(service2).build();

        StateManager stateManager = new StateManager(vrp);
        stateManager.updateSkillStates();
        stateManager.informInsertionStarts(Arrays.asList(route),null);

        skillConstraint = new HardSkillConstraint(stateManager);
    }

    @Test
    public void whenJobToBeInsertedRequiresSkillsThatNewVehicleDoesNotHave_itShouldReturnFalse(){
        JobInsertionContext insertionContext = new JobInsertionContext(route,vrp.getJobs().get("s3"),vehicle,route.getDriver(),0.);
        assertFalse(skillConstraint.fulfilled(insertionContext));
    }

    @Test
    public void whenJobToBeInsertedRequiresSkillsThatVehicleHave_itShouldReturnTrue(){
        JobInsertionContext insertionContext = new JobInsertionContext(route,vrp.getJobs().get("s4"),vehicle,route.getDriver(),0.);
        assertTrue(skillConstraint.fulfilled(insertionContext));
    }

    @Test
    public void whenRouteToBeOvertakenRequiresSkillsThatVehicleDoesNotHave_itShouldReturnFalse(){
        JobInsertionContext insertionContext = new JobInsertionContext(route,vrp.getJobs().get("s3"),vehicle2,route.getDriver(),0.);
        assertFalse(skillConstraint.fulfilled(insertionContext));
    }

    @Test
    public void whenRouteToBeOvertakenRequiresSkillsThatVehicleDoesNotHave2_itShouldReturnFalse(){
        JobInsertionContext insertionContext = new JobInsertionContext(route,vrp.getJobs().get("s4"),vehicle2,route.getDriver(),0.);
        assertFalse(skillConstraint.fulfilled(insertionContext));
    }

    @Test
    public void whenRouteToBeOvertakenRequiresSkillsThatVehicleDoesHave_itShouldReturnTrue(){
        JobInsertionContext insertionContext = new JobInsertionContext(route,vrp.getJobs().get("s4"),vehicle,route.getDriver(),0.);
        assertTrue(skillConstraint.fulfilled(insertionContext));
    }

}
