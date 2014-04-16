Team 25
======

##SimCity201 Project Repository

###Student Information

  + Colin Cammarano
    + USC Email: cammaran@usc.edu
    + USC ID: 8085947576
  
  + Kristen McNeal
    + USC Email: kamcneal@usc.edu
    + USC ID: 8028718737

  + Cathy Ji
    + USC Email: catherhj@usc.edu
    + USC ID: 6862729897
  
  + Yunying Tu
    + USC Email: yunyingt@usc.edu
    + USC ID: 8634605550
  
  + Jeff Redland
    + USC Email: 
    + USC ID:

###Resources
  + [Restaurant v1](http://www-scf.usc.edu/~csci201/readings/restaurant-v1.html)
  + [Agent Roadmap](http://www-scf.usc.edu/~csci201/readings/agent-roadmap.html)

###City Information
  + Our city has 5 restaurants(of different food and style), 1 bank, 1 market, 40 houses and apartments, one bus route, four bus stops
  + Things that work well:
    + Bank
    + Colin, Cathy, and Yunying's restaurants
    + Houses and Apartments
    + Transportation system
  + Things that do not work well
    + Kristen and Jeff's restaurants
    + Market

###Job Distribution
  + Colin Cammarano: PersonAgent, Personal restaurant project, Base Role class, PersonPanel, AnimationPanel, global timer, City and Building views, A*, 
    + PersonAgent: perfectly functioning
    + Personal restaurant project: Perfectly functioning
    + Role: perfectly functioning
    + PersonPanel: perfectly functioning
    + AnimationPanel: Have the basic layout. Still working on adding animations. This will be a team effort. We will have it finished in v2.
    + City and Building views: Have the basic layouts, but waiting on finished agent code to create animations. Switching between buildings works cleanly.
    + Global timer: Perfectly functioning (fires in "30 minute" intervals every 12.5 seconds) with support for multiple days of the week.
    + A*: Have the algorithm implemented, with a new tile class and specific traversal classes to handle the pathfinding needs for the different GUI classes. Still need to work on integrating the algorithm with real gui. This will be done once the animations are totally implemented. We will have this graded in v2.
  + Cathy Ji: Bank, JiRestaurant
    + Bank: perfectly functioning
    + JiRestaurant: perfectly functioning
  + Yunying Tu: Transportation, BuildingPanel, Configuration, TuRestaurant
    + Transportation: perfectly functioning
      + The person will drive if he has a car. Or he will walk to the nearest bus stop, take bus, and then walk from the bus stop to the destination building. If the transportation stops, he will simply walk to the building
    + BuildingPanel: If you click on the button, you can see the employees in the building. But the hire/fire/break function is not working yet. These functions will be implemented in v2.
    + Configuration: perfectly functioning. 
      + Please refer to "Running program" part for details
    + TuRestaurant: perfectly functioning
  + Kristen McNeal: House, McNealRestaurant
    + House: perfectly functioning
    + McNealRestaurant: not functioning
  + Jeff Redland: Market
    + Market: not functioning. 
    + RedlandRestaurant: not functioning

###Test Cases
  + Please refer to "Running the program" part. We provide one full configuration and five different restaurant configurations for use.

###Running the program:
  + Please run the program as java application. Then go to presets tab, and choose "Full" from the ComboBox.
    + This configuration includes all people that the restaurants and bank need to function normally (One host, one regular waiter, one share-data waiter, one cook, one cashier) (Two bank tellers and one security)
    + Things that lack in this configuration:
      + Jeff's restaurant roles (currently all are deffered to Colin's restaurant)
      + Kristen's waiter roles
      + Market is not fully functioning
  + Other five configurations named after our team members
    + Each configuration provides the insight into one of our team member's restaurant configuration
    + You can see how a person will do his/her housework, choose transportation, work and all others.

###Unit Testing:
  + The unit testing is in all respective folders. You can find the unit testing in the following way:
    + transportation.tests: BusTest, BusStopTest, PassengerTest
    + restaurant folder: in each team member's folder.tests, you will find their unit tests, including cashier tests, cook test, and share-data waiter test
    + bank.tests: bank agent test, bank patron test, bank teller test, security test
    + housing.tests: house agent test, house person test, landlord test
    + global.tests: Tests for the person agent, specifically adding new tasks and tracking the time (and other things to do), followed by tests to act on these aforementioned tasks.
    + gui: ui tests
	
###Things to note
  + Colin Cammarano:
    + Several of my commits (totalling in tens of thousands of additions or so) are not tracked in the "Contributors" section because the USC CSCI 201L organization did not track my GitHub account. I cleared this witth Jared, our team mentor and my commits (under my name Colin Cammarano) do appear under the commits menu on the upper left side of the repository status bar. Please read this before assigning my team grade because the "Contributors" section is INACCURATE. Thanks!
