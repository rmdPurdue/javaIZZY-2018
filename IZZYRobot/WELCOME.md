# Welcome to IZZY! 

You are viewing a project that has evolved throughout many years to provide 
learning experiences for students in the space of Theatrical Engineering.

## A Brief Overview:

IZZY is a robot that can drive heavy equipment around the stage.
This is currently accomplished through inductive line following, 
relative location data, and obstacle detection. Work is being done 
to incorporate obstacle avoidance, absolute positioning, and then 
remote path planning. 

IZZY has multiple components, this code is run on the robot and handles
movement and line following. There is another program that runs obstacle
detection code and communicates that information through a local network
socket. Lastly, in order to tie everything together, a program we affectionately
call "Mother" is run as a control interface. The name comes from our connection
validation code where we say IZZY is listening for Mother's heartbeat. You 
must have Mother running in order to control IZZY. It is also recommended
that Mother be started first so log messages can be viewed on the Mother
interface. 

Historically, the team will tackle different aspects so we can work in parallel.
For example, one member might work on the Mother code, one member may work on 
obstacle detection, another may work in the hardware space, another may work on
an expansion or new feature entirely. 

## How to Use

See the README.md file

## What needs to be done?

**Updated 4/26/23**
