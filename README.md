## Adding Blocks

### ⚠ Always run editor as admin

### 1. Add Workflow Block

1. Add block implementation inherited from
   class [WorkflowBlock](JEBuilder/WorkflowBuilder/src/main/java/blocks/WorkflowBlock.java)
   in [Blocks](JEBuilder/WorkflowBuilder/src/main/java/blocks) in JEBuilder/WorkflowBuilder
2. Add method to add block in workflow service from front config to Block mapping in
   builder [Workflow service](JEBuilder/ProjectBuilder/src/main/java/io/je/project/services/WorkflowService.java)
3. Add conversion of the block to BPMN using
   the [Model Builder](JEBuilder/WorkflowBuilder/src/main/java/builder/ModelBuilder.java)
4. Add equivalent service task of the block in [Service Tasks](JERunner/WorkflowEngine/src/main/java/io/je/serviceTasks)
5. Add process flow element of the block
   to [Workflow => BPMN builder](JEBuilder/WorkflowBuilder/src/main/java/builder/JEToBpmnMapper.java)

### 2. Add Rule Block

1. Create a class in the rule builder for the
   block [Blocks](JEBuilder/rulebuilder/src/main/java/io/je/rulebuilder/components/blocks) overriding the getExpression
   method and the getAsOperandExpression method ( not always , depending on the block in
   question ) [Get Expression Location](JEBuilder/rulebuilder/src/main/java/io/je/rulebuilder/components/blocks/Block.java)
2. Add attribute mapping of the new block if any to
   the [Attribute Mapping Folder](JEBuilder/rulebuilder/src/main/java/io/je/rulebuilder/config/AttributesMapping.java)
3. In case of a mathematical block we can the function to [Utilities](Utilities/src/main/java/utils/maths)
4. Add block to [Block factory](JEBuilder/rulebuilder/src/main/java/io/je/rulebuilder/components/BlockFactory.java)

# RoadMap

- [ ] 
- [ ] Improve development environment (independent from SIOTH installation)
- [ ] Check Identity Vs Equality in drools( Review Project Container code / remove facts hashMap ?)
- [ ] Refactor blocks attribute mapping
- [ ] Unify the convention in Rules/ Workflows (build/status/deleting DRL/BPMN files...)
- [ ] Integrate Lombok in the code
- [ ] Optimize collections used (HashMap with another data structure)
- [ ] Refactor Hard coded magic strings, log messages and operation IDs (rules)
- [ ] Integrate a Mapper for DTOs for better readability
- [ ] Check TODO list
- [ ] Optimize exception handling
- [ ] Apply rest API best practices in all the endpoints
- [ ] Change all attributes sent to use the JAVA convention (camel case)
- [ ] Add more messages when compiling rules
- [ ] Refactor blocks attribute mapping
- [ ] Unify the convention in Rules/ Workflows (build/status/deleting DRL/BPMN files...)
- [ ] Integrate Lombok in the code
- [ ] Optimize collections used (HashMap with another data structure)
- [ ] Refactor Hard coded magic strings, log messages and operation IDs (rules)
- [ ] Integrate a Mapper for DTOs for better readability

### Questions

-

```plantuml
@startuml
participant Actor
Actor -> DataModelListener : startListening
activate DataModelListener
DataModelListener -> JELogger : debug
activate JELogger
JELogger -> LoggerUtils : debug
activate LoggerUtils
LoggerUtils --> JELogger
deactivate LoggerUtils
JELogger -> LoggerUtils : getLogMessage
activate LoggerUtils
create LogMessage
LoggerUtils -> LogMessage : new
activate LogMessage
LogMessage --> LoggerUtils
deactivate LogMessage
LoggerUtils --> JELogger
deactivate LoggerUtils
JELogger -> JELogger : publishLogMessage
activate JELogger
JELogger -> LoggerUtils : getLogLevel
activate LoggerUtils
LoggerUtils --> JELogger
deactivate LoggerUtils
JELogger -> LoggerUtils : logLevelIsEnabled
activate LoggerUtils
LoggerUtils --> JELogger
deactivate LoggerUtils
JELogger -> ZMQLogPublisher : publish
activate ZMQLogPublisher
ZMQLogPublisher --> JELogger
deactivate ZMQLogPublisher
JELogger --> JELogger
deactivate JELogger
JELogger --> DataModelListener
deactivate JELogger
DataModelListener -> DataModelListener : readInitialValues
activate DataModelListener
DataModelListener -> DataModelRequester : readInitialValues
activate DataModelRequester
DataModelRequester -> JELogger : trace
activate JELogger
JELogger --> DataModelRequester
deactivate JELogger
DataModelRequester -> ZMQRequester : sendRequest
activate ZMQRequester
ZMQRequester --> DataModelRequester
deactivate ZMQRequester
DataModelRequester -> JELogger : trace
activate JELogger
JELogger --> DataModelRequester
deactivate JELogger
DataModelRequester -> JELogger : error
activate JELogger
JELogger --> DataModelRequester
deactivate JELogger
DataModelRequester --> DataModelListener
deactivate DataModelRequester
create JEData
DataModelListener -> JEData : new
activate JEData
create JEObject
JEData -> JEObject : new
activate JEObject
JEObject --> JEData
deactivate JEObject
JEData --> DataModelListener
deactivate JEData
DataModelListener -> RuntimeDispatcher : injectData
activate RuntimeDispatcher
RuntimeDispatcher -> JELogger : trace
activate JELogger
JELogger --> RuntimeDispatcher
deactivate JELogger
RuntimeDispatcher -> RuntimeDispatcher : λ→
activate RuntimeDispatcher
RuntimeDispatcher -> InstanceManager : createInstance
activate InstanceManager
InstanceManager -> InstanceManager : getInstanceModel
activate InstanceManager
create InstanceModel
InstanceManager -> InstanceModel : new
activate InstanceModel
InstanceModel --> InstanceManager
deactivate InstanceModel
InstanceManager --> InstanceManager
deactivate InstanceManager
InstanceManager -> ClassRepository : getClassById
activate ClassRepository
ClassRepository --> InstanceManager
deactivate ClassRepository
create InstanceCreationFailed
InstanceManager -> InstanceCreationFailed : new
activate InstanceCreationFailed
create JEException
InstanceCreationFailed -> JEException : new
activate JEException
JEException --> InstanceCreationFailed
deactivate JEException
InstanceCreationFailed --> InstanceManager
deactivate InstanceCreationFailed
create InstanceCreationFailed
InstanceManager -> InstanceCreationFailed : new
activate InstanceCreationFailed
create JEException
InstanceCreationFailed -> JEException : new
activate JEException
JEException --> InstanceCreationFailed
deactivate JEException
InstanceCreationFailed --> InstanceManager
deactivate InstanceCreationFailed
InstanceManager --> RuntimeDispatcher
deactivate InstanceManager
RuntimeDispatcher -> DataModelListener : getProjectsSubscribedToTopic
activate DataModelListener
DataModelListener --> RuntimeDispatcher
deactivate DataModelListener
RuntimeDispatcher -> RuleEngineHandler : injectData
activate RuleEngineHandler
RuleEngineHandler -> RuleEngine : assertFact
activate RuleEngine
RuleEngine -> ProjectContainerRepository : getProjectContainer
activate ProjectContainerRepository
ProjectContainerRepository --> RuleEngine
deactivate ProjectContainerRepository
RuleEngine -> ProjectContainer : insertFact
activate ProjectContainer
ProjectContainer --> RuleEngine
deactivate ProjectContainer
RuleEngine --> RuleEngineHandler
deactivate RuleEngine
RuleEngineHandler -> JELogger : warn
activate JELogger
JELogger -> LoggerUtils : warn
activate LoggerUtils
LoggerUtils --> JELogger
deactivate LoggerUtils
JELogger -> LoggerUtils : getLogMessage
activate LoggerUtils
LoggerUtils --> JELogger
deactivate LoggerUtils
JELogger -> JELogger : publishLogMessage
activate JELogger
JELogger --> JELogger
deactivate JELogger
JELogger --> RuleEngineHandler
deactivate JELogger
RuleEngineHandler --> RuntimeDispatcher
deactivate RuleEngineHandler
RuntimeDispatcher --> RuntimeDispatcher
deactivate RuntimeDispatcher
RuntimeDispatcher -> JELogger : error
activate JELogger
JELogger --> RuntimeDispatcher
deactivate JELogger
RuntimeDispatcher --> DataModelListener
deactivate RuntimeDispatcher
DataModelListener --> DataModelListener
deactivate DataModelListener
return
@enduml

```

if the injected data is of type JEObject how does drools get the type of the class to be evaluated??
why would u load only classes used if the user is gonna need them when writing script task
class loading twice issue
class loader
ZMQ not working constantly
nested inheritance produces duplication of the class
class1 => class2 > class1 boucle infinie
why no await for process execution
script task buggy
class loader
![img_2.png](img_2.png)

