# Welcome to IZZY! 

You are viewing a project that has evolved throughout many years to provide 
learning experiences for students in the space of Theatrical Engineering.

## A Brief Overview:

IZZY is a robot that can drive heavy equipment around the stage.
This is currently accomplished through inductive line following, 
relative location data, and obstacle detection. Work is being done 
to incorporate obstacle avoidance, absolute positioning, and then 
remote path planning. 

IZZY has multiple components, the code in this repository is run on the robot 
and handles movement and line following. There is another program that runs 
obstacle detection code and communicates that information through a local network
socket. Lastly, in order to tie everything together, a program we affectionately
call "Mother" is run as a control interface. The name comes from our connection
validation code where we say IZZY is listening for Mother's heartbeat. You 
must have Mother running in order to control IZZY. It is also recommended
that Mother be started first so log messages can be viewed on the Mother
interface. 

Historically, the team will tackle different aspects of the project, so we can 
work in parallel. For example, one member might work on the Mother code, one 
member may work on obstacle detection, another may work in the hardware space,
another may work on an expansion or new feature entirely. Since this project has
spanned multiple semester, we've learned a lot of important lessons. Learn from our
mistakes... **you have been warned :)**. Important lessons we
have learned from semester's past are:

1) Create weekly progress reports (what you did, what you're going to do, what problems you faced (and if/how you solved them)
   1) Hold each other accountable
2) Start the semester with a planning meeting where you decide where the project is going that semester
   1) New area of research? --> Lit Review
   2) Existing area of research? --> See what work has already been done
3) Start the semester with STRONG project management planning and recruiting efforts
   1) Usually involves in-person conversations and written goals
   2) May need to onboard new people
4) When creating deadlines/goals, always assume there will be unexpected problems and leave plenty of buffer in schedule

## Next Steps

**Updated 5/1/23**

1) Fine-tune obstacle detection code (ObstacleDetectionController)
   1) Change default values of PID and threshold to match tape on IZZY
2) Start obstacle avoidance program inside this package
   1) Instead of having to start multiple programs
3) Make log configuration dynamic (currently hard code Mother IP in log4j2.xml)
4) Fix "status light" in Mother
5) Fix what is not closing on exit in Mother
6) HARDWARE (urgent): create low-battery cut-off circuit
   1) See ["Over-Discharging LiPo Batteries"](https://power.tenergy.com/lipo-safety-warnings/)
7) Migrate Mother code to Maven

## Future Goals

1) Can we send current/signals down the wire
2) How small can we make the wire
3) Can we remove the wire/switch back and forth between control methods
4) Can we visualize plot (and incorporate route-planning from Mother)
5) Can we move side to side without pivoting

## Sequence Diagram

If you are a visual learner, perhaps this sequence diagram can be of use to you. The file is located in the /assets folder.
This is not automatically updated. Last update was: 5/1/23

![IntelliJ Build Configuration](assets/IntelliJ Config.png "IntelliJ Build Configuration Screenshot")

## How to Use IZZY (run the code)

See the README.md file