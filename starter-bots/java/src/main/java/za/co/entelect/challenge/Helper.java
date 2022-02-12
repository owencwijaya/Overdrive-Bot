package za.co.entelect.challenge;

import za.co.entelect.challenge.entities.Car;
import za.co.entelect.challenge.entities.GameState;
import za.co.entelect.challenge.entities.Lane;
import za.co.entelect.challenge.enums.PowerUps;
import za.co.entelect.challenge.enums.Terrain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.Math.min;
import static java.lang.Math.max;

public class Helper {
    private final Car myCar;
    private final GameState gameState;

    public Helper(Car myCar, GameState gameState) {
        this.myCar = myCar;
        this.gameState = gameState;
    }

    public int min3(int a, int b, int c) {
        return (min(a, min(b, c)));
    }

    public int max3(int a, int b, int c){
        return (max(a, max(b, c)));
    }


    public Boolean hasPowerUp(PowerUps powerUpToCheck, PowerUps[] available) {
        for (PowerUps powerUp: available) {
            if (powerUp.equals(powerUpToCheck)) {
                return true;
            }
        }
        return false;
    }

    public int Obstacles(List<Object> Lane) {
        int count = 0;
        for (int i = 0; i < Lane.size(); i++) {
            if (Lane.get(i).equals(Terrain.MUD) ||
                    Lane.get(i).equals(Terrain.OIL_SPILL)){
                count++;
            } else if (Lane.get(i).equals(Terrain.WALL)){
                count += 10;
            }
        }
        return count;
    }

    public String compareLanes(Car myCar, List<Object> left, List<Object> curr, List<Object> right){
        boolean LPos = false; boolean RPos = false;
        int lCount = 100, rCount = 100, cCount = 100; //asumsi isinya wall semua

        if (myCar.position.lane > 1) {
            lCount = Obstacles(left);
            LPos = true;
        }
        if (myCar.position.lane < 4){
            rCount = Obstacles(right);
            RPos = true;
        }

        cCount = Obstacles(curr);

        if (LPos && RPos) { //kalau memungkinkan untuk pindah kiri dan kanan
            if (min3(lCount, rCount, cCount) == lCount){
                if (lCount == cCount){
                    if (lCount == rCount){
                        return "ALL"; // return ALL untuk membandingkan semua lane
                    } else {
                        return "CURR_LEFT"; // return CURR_LEFT untuk membandingkan lane curr dan kiri
                    }
                } else {
                    return "TURN_LEFT"; // return TURN_LEFT kalau lane kiri yang paling sedikit (langsung belok kiri)
                }
            } else if (min3(lCount, rCount, cCount) == cCount){
                if (cCount == rCount){
                    return "CURR_RIGHT"; // return CURR_RIGHT untuk membandingkan lane curr dan kanan
                } else {
                    return "STAY"; // return STAY untuk stay in lane
                }
            } else {
                return "TURN_RIGHT"; // return TURN_RIGHT kalau belok kanan
            }
        } else if (LPos && !RPos){ //kalau memungkinkan untuk pindah kiri saja
            if (min(lCount, cCount) == lCount){
                if (lCount == cCount){
                    return "CURR_LEFT"; //return CURR_LEFT untuk membandingkan lane curr dan kiri
                } else {
                    return "TURN_LEFT"; //return TURN_LEFT untuk belok kiri
                }
            } else {
                return "STAY"; // return STAY untuk stay in lane
            }
        } else if (!LPos && RPos){ //kalau emmungkinkan untuk pindah kanan saja
            if (min(rCount, cCount) == rCount){
                if (rCount == cCount){
                    return "CURR_RIGHT"; //return CURR_RIGHT untuk membandingkan lane curr dan kanan
                } else {
                    return "TURN_RIGHT"; //return TURN_RIGHT untuk belok kanan
                }
            } else {
                return "STAY"; // return STAY untuk stay in lane
            }
        }
        return "STAY";//return STAY default untuk stay in lane
    }

    public int nextSpeedState (Car targetCar) {
        switch (targetCar.speed) {
            case 0:
                return 3;
            case 3:
            case 5:
                return 6;
            case 6:
                return 8;
            case 8:
                return 9;
            default:
                return targetCar.speed;
        }
    }

    public int prevSpeedState (Car targetCar) {
        switch (targetCar.speed) {
            case 9:
                return 8;
            case 8:
                return 6;
            case 6:
                return 3;
            case 5:
                return 3;
            case 3:
                return 0;
            default:
                return targetCar.speed;
        }
    }


    public int LaneBlock (String direction) {
        int flag = 0;
        if (direction.equals("LEFT")){
            flag = -1;
        } else if (direction.equals("RIGHT")){
            flag = 1;
        }
        List<Lane[]> map = gameState.lanes;
        Lane[] laneList = map.get(myCar.position.lane - 1 + flag); // tidak dikurangi 1 soalnya dia basisnya 0 (-1) dan dia ke kanan (+1)
        int landingPosition = myCar.speed + 1; // harus ngecek ini basis 0 atau engga
        if (laneList[landingPosition].terrain.equals(Terrain.BOOST)) {
            return 1;
        } else if (laneList[landingPosition].terrain.equals(Terrain.LIZARD)) {
            return 2;
        } else if (laneList[landingPosition].terrain.equals(Terrain.TWEET)) {
            return 3;
        } else if (laneList[landingPosition].terrain.equals(Terrain.OIL_POWER)) {
            return 4;
        } else if (laneList[landingPosition].terrain.equals(Terrain.EMP)) {
            return 5;
        } else {
            return 9;
        }
    }

    public int accelerateLaneBlock (Car myCar, GameState gameState) {
        List<Lane[]> map = gameState.lanes;
        Lane[] laneList = map.get(myCar.position.lane - 1); // dikurangi 1 soalnya dia basisnya 0
        int landingPosition = myCar.position.block +  nextSpeedState(myCar) + 1; // harus ngecek ini basis 0 atau engga
        if (laneList[landingPosition].terrain.equals(Terrain.BOOST)) {
            return 1;
        } else if (laneList[landingPosition].terrain.equals(Terrain.LIZARD)) {
            return 2;
        } else if (laneList[landingPosition].terrain.equals(Terrain.TWEET)) {
            return 3;
        } else if (laneList[landingPosition].terrain.equals(Terrain.OIL_POWER)) {
            return 4;
        } else if (laneList[landingPosition].terrain.equals(Terrain.EMP)) {
            return 5;
        } else {
            return 9;
        }
    }

    public int obstacleLandingBlock(String direction) {
        int flag = 0;
        if (direction.equals("LEFT")){
            flag = -1;
        } else if (direction.equals("RIGHT")){
            flag = 1;
        }
        List<Lane[]> map = gameState.lanes;
        Lane[] laneList = map.get(myCar.position.lane - 1 + flag); // tidak dikurangi 1 soalnya dia basisnya 0 (-1) dan dia ke kanan (+1)
        int landingPosition = myCar.speed + 1; // harus ngecek ini basis 0 atau engga
        if (laneList[landingPosition].terrain.equals(Terrain.OIL_SPILL)) {
            return 1;
        } else if (laneList[landingPosition].terrain.equals(Terrain.MUD)) {
            return 2;
        } else if (laneList[landingPosition].terrain.equals(Terrain.WALL)) {
            return 3;
        } else {
            return 0;
        }
    }

    public int countPowerUps(List<Object> laneList){
        int count = 0;
        for (int i = 0; i < laneList.size(); i++) {
            if (laneList.get(i).equals(Terrain.OIL_POWER) ||
                    laneList.get(i).equals(Terrain.EMP) ||
                    laneList.get(i).equals(Terrain.BOOST) ||
                    laneList.get(i).equals(Terrain.LIZARD) ||
                    laneList.get(i).equals(Terrain.TWEET)) {
                count++;
            }
        }
        return count;
    }

  public boolean hasCyberTruck(int flag) {
      List<Lane[]> map = gameState.lanes;
      Lane[] laneList = map.get(myCar.position.lane - 1 + flag);
      int count = 0;
      for (int i = 0; i < laneList.length; i++) {
          if (laneList[i].isOccupiedByCyberTruck){
              return true;
          }
      }
      return false;
  }
}