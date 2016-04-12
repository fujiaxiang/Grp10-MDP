1. Straight Path vs. Diagonal Path
    As diagonal path finder and runner are added later, we did not incorporate the choice between these two into the UI. Instead,
    we have two versions of code. The original purpose was to enable us to quickly revert to straight path version should the
    newer version go wrong
    To switch between these two, you need to do the following two things:
    a. In class Controller, at the end of startRobot() method, choose between runShortestPath() and runDiagonalPath() (the two are
        already coded, you just need to comment one of them)
        One optional thing is to choose cost calculation method at a few lines above.
    b. In class MazeExplorer, at the end of explore() method, choose between getReadyForDiagonalShortestPath()
        and getReadyForShortestPath()

2. Second round exploration
    The second round exploration eables the robot to explore what's left after first round of wall-hugging exploration.
    To enable or disable this:
        go to class MazeExplorer, near the end of explore() method, comment or uncomment runToUnknownPlace()

3. Time limit and coverage limit
    The code to react to these two limits have been disabled as we wanted to pursue perfect score in leaderboard challenge
    since our team were doing quite well
    To enable this:
        go to class MazeExplorer, in the middle of explore() method, in the while loop, uncommment the two relevant if statements

4. Testing robot shortest path running without actual exploration phase (with simulated exploration)
    A method in class Controller has allowed us to achieve this. It's called startRobotTest().
    To go this:
        go to Main class and main() method, find startRobot and replace with startRobotTest()