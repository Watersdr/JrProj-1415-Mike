## Android test scripts
## Test ability to see status of milestones of the project
## Only works by manually changing isPasswordValid function in LoginActivity.java to >1

find("1413993915191.png")
click("1413993915191.png")
wait(5)
type("test2@test.com" + Key.TAB + "test")
click("1415306750004.png")
sleep(10)
click("1413994136190.png")
wait(5)
find("1413937690035.png")
click("1413937698349.png")
wait(5)
find("1415306929140.png")
click("1415306943664.png")
wait(5)
if exists("1413937956682.png"):
    print("It worked")
else:
    print("It didn't work")

find("1415308071645.png")
click("1415308079474.png")
click("1415308092275.png")
    


wait(5)


## Test ability to see status of the project
find("1413993915191.png")
click("1413993915191.png")
wait(5)
type("test2@test.com" + Key.TAB + "test")
click("1415306750004.png")
wait(10)
find("1413935779601.png")
click("1413935786186.png")
if exists("1413935809730.png"):
    print("It worked")
else:
    print("It didn't work")
wait(3)

find("1415308071645.png")
click("1415308079474.png")
click("1415308092275.png")

wait(5)

## Test the ability to update the status of a feature
find("1413993915191.png")
click("1413993915191.png")
wait(5)
type("test2@test.com" + Key.TAB + "test")
click("1413994040202.png")
wait(10)
find("1413935779601.png")
click("1413935786186.png")
wait(5)
find("1413937690035.png")
click("1413937698349.png")
wait(7)
find("1415308265161.png")
click("1415308270336.png")
wait(5)
find("1413938516555.png")
click("1413938523397.png")
wait(5)
click("1415308313732.png")
wait(5)
find("1413938557144.png")
click("1413938557144.png")
wait(2)
find("1413938799187.png")
click("1413938806937.png")
if exists("1413938967238.png"):
    print("It worked")
else:
    print("It didn't work")

print("Finished")