from dataclasses import (
  dataclass,
  field
)
from typing import List
from constant import PEAK_TROUGH, CONF_BETA, TIME_THRESHOLD, CONFIDENCE_HAMMING
from mock import getPressMiddleDouble, getPressUpDouble, getPressDownDouble, getPressMiddleSingle, getPressUpSingle, getPressDownSingle
from mock import getFuse
import time as tm
from util import max_hamming

''' 
A reimplementation of the JAVA code of recognition
Some notification: 
    1. I change all the long into int in python
    2. I define all the data class in JAVA into data class in python
'''

@dataclass
class spot:
    
    identity: int = 0
    time: int = 0 
    where: List[List[List[int]]] = field(default_factory = List[List[List[int]]])
    
    def __init__(self, _identity: int = 0, _time: int = 0, _where: List[List[List[int]]] = []):
        self.identity = _identity
        self.time = _time
        self.where = _where


@dataclass
class pixSpot:

    x: int = 0
    y: int = 0
    capacity: int = 0
    time: int = 0

    def __init__(self, _x = 0, _y = 0, _capacity = 0, _time = 0):
        self.x = _x
        self.y = _y
        self.capacity = _capacity
        self.time = _time
        
@dataclass
class spotGroup:

    main_spot: pixSpot = None
    second_spot: pixSpot = None
    chosen: bool = False 
    spot_array_list: List[pixSpot] = field(default_factory = List[pixSpot]) 

    def __init__(self, _main_spot: pixSpot = None, _second_spot: pixSpot = None, _chosen: bool = False,
                     _pix_spot: pixSpot = None, spot_array_list: List[pixSpot] = []):
        
        self.main_spot = _main_spot
        self.second_spot = _second_spot
        self.chosen = _chosen
        self.spot_array_list = spot_array_list
        self.spot_array_list.append(_pix_spot)

@dataclass
class movementGroup:

    start: int = 0
    end: int = 0
    groupArrayList: List[spotGroup] = field(default_factory = List[spotGroup]) 
    alive: bool = True

    def __init__(self, _group_item: spotGroup = None, _start: start = 0):
        self.groupArrayList = []
        self.groupArrayList.append(_group_item)
        self.start = _start
    


@dataclass
class spotGroupAssist:

    mainSpot: pixSpot = None
    parent: movementGroup = None
    selected: bool = False

    
    def __init__(self, _main_spot: pixSpot = None, _parent: movementGroup = None):
        self.mainSpot = _main_spot
        self.parent = _parent
        # default settings
        self.selected = False


# @dataclass
class patternGroup:

    time: int = 0
    force: int = 0
    groupArrayList: List[spotGroup] = field(default_factory = List[spotGroup]) 
    
    def __init__(self):
        self.time = 0


@dataclass
class touch:

    capacity: int
    time: int 
    
    def __init__(self, cap: int, t: int):
        capacity = cap
        time = t
        
    
    
# where to go
touchGroups = [] # ArrayList<movementGroup> 
totalAlive = 0
myTouch11 = [] # list of touch
isDebugRaw = False
z = 0
touchx = 0
touchy = 0
frame_num = 0;
data_arr_list = [[] for _ in range(0, 15)]
spots = [spot() for _ in range(0, 100)] # list of spot
spotnum = 1
mydrawStep = 0


def match(currentGroups : List[spotGroup], previousGroups: List[spotGroupAssist]):
    for cg in currentGroups:
        if len(previousGroups) == 0:
            touchGroups.append(movementGroup(cg, z + 1))
            continue
        distance = 1000000
        for pg in previousGroups:
            if dis(pg, cg) < distance:
                if pg.mainSpot.capacity * cg.mainSpot.capacity > 0:
                    distance = dis(pg, cg)
                    selectedPG = pg
        
        if distance > 20:
            touchGroups.append(movementGroup(cg, z + 1))
            continue
        
        selectedPG.parent.groupArrayList.append(cg)
        previousGroups.remove(selectedPG)
    
    for pg in previousGroups:
        pg.parent.alive = False
        pg.parent.end = z + 1
    
    

def makePattern(touchGroup: movementGroup = None):
        i = 0
        for sg in touchGroup.groupArrayList:
            data[i] = sg
            i += 1
        PeakAndTrough = []
        mid = 1
        hi = 2
        lo = 0
        for hi in range(2, len(data)):
            # 先令data[lo]不等于data[mid]
            while (mid < len(data) and data[mid].mainSpot.capacity == data[lo].mainSpot.capacity):
                mid += 1

            hi = mid + 1

            # 令data[hi]不等于data[mid]
            while (hi < len(data) and data[hi].mainSpot.capacity == data[mid].mainSpot.capacity):
                hi += 1

            if hi >= len(data):
                break

            # 检测是否为峰值
            if (data[ mid ].mainSpot.capacity > data[ lo ].mainSpot.capacity and data[ mid ].mainSpot.capacity > data[ hi ].mainSpot.capacity):
                PeakAndTrough[ mid ] = 1       # 1代表波峰
        
            elif(data[ mid ].mainSpot.capacity < data[lo].mainSpot.capacity and data[mid].mainSpot.capacity < data[ hi ].mainSpot.capacity):
                PeakAndTrough[ mid ] = -1      # -1代表波谷
            
            lo = mid
            mid = hi
        
        for j in range(0, len(data) - 1):
            if PeakAndTrough[j] * PeakAndTrough[j + 1] < 0:
                if abs( PeakAndTrough[j] - PeakAndTrough[j + 1]) < PEAK_TROUGH:
                    # 50还需要确定具体多大
                    PeakAndTrough[j] = PeakAndTrough[j + 1] = 0
        
        for j in rang(0, len(data)):
            if PeakAndTrough[j] == 1:
                tmp = j
                peak = data[j].mainSpot.capacity
                diff = 0 # Not initialized originally in JAVA
                if data[j - 1].mainSpot.capacity < data[j + 1].mainSpot.capacity:
                    diff = peak - data[j - 1].mainSpot.capacity
                else:
                    diff = peak - data[j + 1].mainSpot.capacity
                diff = max(diff, 100)
                while( peak - data[ tmp ].mainSpot.capacity < diff ):
                    PeakAndTrough[ tmp ] = 1
                    tmp -= 1
                tmp = j + 1
                while( peak - data[ tmp ].mainSpot.capacity < diff ):
                    PeakAndTrough[ tmp ] = 1
                    tmp += 1
                j = tmp
        
def explore(data: List[List[int]], isIn: List[List[int]],
                xx: int, yy: int, myGroup: spotGroup, time: int):

        # print("----------------- entering explore -------------------")
        if( xx > 35 or yy > 17 or xx < 0 or yy < 0 ):
            return
        if( data[ xx ][ yy ] <= 400 and data[ xx ][ yy ] >= -400 ):
            return
        if( isIn[ xx ][ yy ] == True ):
            return
        
        myGroup.spot_array_list.append(pixSpot(xx, yy, data[ xx ][ yy ], time))
        isIn[ xx ][ yy ] = True

        if( yy + 1 <= 17 ):
            if( data[ xx ][ yy ] * data[ xx ][ yy + 1 ] > 0 ):
                explore( data, isIn, xx , yy + 1, myGroup, time)
        
        if( yy - 1 >= 0 ):
            if( data[ xx ][ yy ] * data[ xx ][ yy - 1 ] > 0 ):
                explore( data, isIn, xx , yy - 1, myGroup, time)
        
        if( xx + 1 <= 35 ):
            if( data[ xx ][ yy ] * data[ xx + 1 ][ yy ] > 0 ):
                explore( data, isIn, xx + 1 , yy, myGroup, time)
        
        if( xx - 1 >= 0 ):
            if( data[ xx ][ yy ] * data[ xx - 1 ][ yy ] > 0 ):
                explore( data, isIn, xx - 1 , yy , myGroup, time)
        
def dis (g: spotGroupAssist, curSelGroup: spotGroup) -> int:
        return     (g.mainSpot.x - curSelGroup.mainSpot.x ) * ( g.mainSpot.x - curSelGroup.mainSpot.x ) + \
             ( g.mainSpot.y - curSelGroup.mainSpot.y ) * ( g.mainSpot.y - curSelGroup.mainSpot.y )

def printGroups(touchGroups: List[movementGroup]):

    for touchGroup in touchGroups:
        data = [] # list of spotgroup
        i = 0
        for sg in touchGroup.groupArrayList:
            data[ i ] = sg
            i += 1
        
        PeakAndTrough = [] # list of int
        lo = 0
        mid = 1
        hi = 2
        for hi in range(2, len(data)):
            while (mid < len(data) and data[mid].mainSpot.capacity == data[lo].mainSpot.capacity):
                mid += 1

            hi=mid+1
            while (hi < len(data) and data[hi].mainSpot.capacity == data[mid].mainSpot.capacity):
                hi += 1
            
            if hi >= len(data):
                break
            
            if (data[ mid ].mainSpot.capacity > data[ lo ].mainSpot.capacity and data[ mid ].mainSpot.capacity > data[ hi ].mainSpot.capacity):
                PeakAndTrough[ mid ] = 1       # 1
            elif(data[ mid ].mainSpot.capacity < data[lo].mainSpot.capacity and data[mid].mainSpot.capacity < data[ hi ].mainSpot.capacity):
                PeakAndTrough[ mid ] = -1      # -1
            
            lo = mid
            mid = hi
        
        for j in range(0, len(data) - 1):
            if( PeakAndTrough[ j ] * PeakAndTrough[ j + 1] < 0 ):
                if( abs( PeakAndTrough[ j ] - PeakAndTrough[ j + 1 ]) < PEAK_TROUGH):
                    PeakAndTrough[ j ] = PeakAndTrough[ j + 1 ] = 0
                
        for j in range(0, len(data)):
            if( PeakAndTrough[ j ] == 1 ):
                tmp = j
                peak = data[j].mainSpot.capacity
                diff = 0 # Unitizialized originally in JAVA
                if( data[ j - 1 ].mainSpot.capacity < data[ j + 1 ].mainSpot.capacity ):
                    diff = peak - data[ j - 1 ].mainSpot.capacity
                else:
                    diff = peak - data[ j + 1 ].mainSpot.capacity
                diff = max( diff, 100 )
                diff = min( diff, 150 )
                while( peak - data[ tmp ].mainSpot.capacity <= diff and peak - data[ tmp ].mainSpot.capacity >= 0 ):
                    PeakAndTrough[ tmp ] = 1
                    tmp -= 1
                
                tmp = j + 1
                while( peak - data[ tmp ].mainSpot.capacity <= diff and peak - data[ tmp ].mainSpot.capacity >= 0 ):
                    PeakAndTrough[ tmp ] = 1
                    tmp += 1
                
                j = tmp
            

def analyzeGroups(touchGroups: List[movementGroup] = None) -> bool:
    result = False
    global myTouch11
    if mydrawStep == 1:
        myTouch11 = [[] for _ in range(0, len(touchGroups))]
    for idx, touchGroup in enumerate(touchGroups):
        data = [None for _ in range(0, len(touchGroup.groupArrayList))] # list of spotgroup
        i = 0
        for sg in touchGroup.groupArrayList:
            data[ i ] = sg
            i += 1

        PeakAndTrough = [0 for _ in range(0, len(data))] # list of int
        lo = 0
        mid = 1
        hi = 2
        for hi in range(2, len(data)):
            # data[lo]  data[mid]
            while (mid < len(data) and data[mid].mainSpot.capacity == data[lo].mainSpot.capacity):
                mid += 1
            
            hi = mid+1
            while (hi < len(data) and data[hi].mainSpot.capacity == data[mid].mainSpot.capacity ):
                hi += 1
            
            if (hi >= len(data)):
                break
            
            if (data[ mid ].mainSpot.capacity > data[ lo ].mainSpot.capacity and data[ mid ].mainSpot.capacity > data[ hi ].mainSpot.capacity):
                PeakAndTrough[ mid ] = 1       # 1
            
            elif(data[ mid ].mainSpot.capacity < data[lo].mainSpot.capacity and data[mid].mainSpot.capacity < data[ hi ].mainSpot.capacity   ):
                PeakAndTrough[ mid ] = -1      # -1
            

            lo = mid
            mid = hi
        
        Touches = [] # list of touch
        PeakAndTrough[0] = -1
        PeakAndTrough[len(data) - 1] = -1
        for j in range(0, len(data)): 
            if( PeakAndTrough[ j ] == 1 ):
                tmp = j
                count = 0
                peak = data[j].mainSpot.capacity
                diff = 0 # Uninitialized originally 
                # has some bugs here
                if j < len(data) - 1:
                    if data[ j - 1 ].mainSpot.capacity < data[ j + 1 ].mainSpot.capacity:
                        diff = peak - data[ j - 1 ].mainSpot.capacity
                    else:
                        diff = peak - data[ j + 1 ].mainSpot.capacity
                diff = max( diff, 100 )
                diff = min( diff, 150 )
                while( tmp >= 0 and peak - data[ tmp ].mainSpot.capacity <= diff ):
                    PeakAndTrough[ tmp ] = 1
                    if( peak - data[ tmp ].mainSpot.capacity <= 0):
                        peak = data[ tmp ].mainSpot.capacity
                    tmp -= 1
                    count += 1
                
                while( tmp >= 0 and PeakAndTrough[ tmp ] != -1 ):
                    tmp -= 1
                st = data[ tmp + 1 ].mainSpot.time
                tmp = j + 1

                while( tmp < len(data) and peak - data[ tmp ].mainSpot.capacity <= diff ):
                    PeakAndTrough[ tmp ] = 1
                    if( peak - data[ tmp ].mainSpot.capacity <= 0):
                        peak = data[ tmp ].mainSpot.capacity
                    tmp += 1
                    count += 1
                
                while( tmp < len(data) and PeakAndTrough[ tmp ] != -1 ):
                    tmp  += 1
                et = data[ tmp - 1 ].mainSpot.time
                j = tmp
                newTouch = touch(cap=1, t=1)
                newTouch.capacity = int(round(peak))
                newTouch.time = int(round(abs(et - st))) 
                Touches.append(newTouch)
        
        # print("Touches : " + str(Touches))
        if mydrawStep == 1:
            # print(idx)
            myTouch11[idx] = []
            for t in Touches:
                myTouch11[idx].append(t)
        # print("entering fakeCompare")
        if(fakeCompare( myTouch11[idx], Touches ) < CONFIDENCE_HAMMING ):
            result = True
        
            '''
            double[] _data = new double[len(Touches) * 2]
            for(int u = 0 u < len(Touches)  += 1u) {
                _data[2 * u] = Touches[i].capacity
                _data[2 * u + 1] = Touches[i].time
            }
            double[] r_ = bp.computeOut(_data)
            if(abs(r_[0] - 1.0) < 0.1) result = true
            else {
                double[] target = new double[1]
                if(result == False) {
                    target[0] = 0
                } else {
                    target[0] = 1
                }
                bp.train(_data, target)
            }
            '''
    return result

def compareTouch(touch_1: List[touch], touch_2: List[touch]) -> bool:
    
    j = 0
    if( len(touch_1) > len(touch_2) ):
        return False

    if( len(touch_1) != len(touch_2) ):
        t1 = touch_1[0]
        t2 = touch_2[j]
        while( abs( t1.capacity - t2.capacity ) > 150 or abs( t1.time - t2.time ) > 2 ):
            j += 1
            t2 = touch_2[j]
            if( j > len(touch_2) - len(touch_1) ):
                return False
        
    
    for i in range(0, len(touch_1)):
        t1 = touch_1[i]
        t2 = touch_2[j]
        if( abs( t1.capacity - t2.capacity ) > 150 or abs( t1.time - t2.time ) > 2 ):
            return False
        j += 1
    
    return True


def fakeCompare(touch_1: List[touch], touch_2: List[touch]) -> int:
    '''
    The first is ground truth and the second is the string to be tested
    '''

    # print(touch_1)
    # print(touch_2)
    
    touches_1 = ""
    touches_2 = ""
    t1 = [] # list of char
    t2 = [] # list of char

    i = 0
    for t in touch_1:
        costed = 0
        while t.time - costed >= TIME_THRESHOLD:
            t1.append('1') 
            i += 1
            costed += TIME_THRESHOLD
        t1.append('0')
        
    
    i = 0
    for t in touch_2:
        costed = 0
        while t.time - costed >= TIME_THRESHOLD:
            t2.append('1')
            i += 1
            costed += TIME_THRESHOLD
        t2.append('0')
    
    confidence = 0.0
    touches_1 = "".join(t1)
    touches_2 = "".join(t2)
    # print(touches_1)
    # print(touches_2)
    """
    if touches_2 in touches_1:
        j = 0
        for j in range(0, len(touches_1)):
            if not touches_2 in touches_1[j:]:
                break
        
        j = j - 1
        for k in range(0, len(touches_2)):
            if k + 1 < len(touch_2) and j + k + 1 < len(touch_1):
                if( (touch_2[k].capacity - touch_2[k + 1].capacity) * (touch_1[j + k].capacity - touch_1[j + k + 1].capacity) >= 0 ):
                    confidence += CONF_BETA / (len(touches_2) - 1)
        
        return confidence
    """
    try:
        length = max_hamming(touches_1, touches_2)
        # print(CONF_BETA * length / min(len(touches_2), len(touches_1)))
        return CONF_BETA * length / min(len(touches_2), len(touches_1))
    except:
        return 1.0


def updateTrack(data: List[List[int]], currentGroups: List[spotGroup], time: int):
    
        # 分割聚类
        tmp = 0 # 表示是否有有效触碰点
        currentAlive = 0 # 此帧现存聚类
        availableList = [] # ArrayList<pixSpot> 
        isIn = [[False for _first_dim in range(0, 19)] for _second_dim in range(0, 37)] # 19 * 37
        # time = int(round(tm.time() * 1000)) # long
        for ii in range(0, len(data) - 1):
            for jj in range(0, len(data[ii])):
                if( data[ii][jj] >= 400 or data[ii][jj] <= -400 ):
                    tmp = 1
                    availableList.append(pixSpot(ii, jj, data[ii][jj], time) )
                    isIn[ii][jj] = False
                
                isIn[ii][jj] = False
            
        previousGroups = [] # list of ArrayList<spotGroupAssist> 
        if tmp != 0:
            for mySpot in availableList:
                if isIn[ mySpot.x ][ mySpot.y ] == False:
                    myGroup = spotGroup(spot_array_list = [])
                    explore( data, isIn, mySpot.x, mySpot.y, myGroup, time)
                    maxInt = 0
                    for maxSpot in myGroup.spot_array_list:
                        try:
                            if abs(maxSpot.capacity) > maxInt:
                                myGroup.mainSpot = maxSpot
                                maxInt = maxSpot.capacity
                        except:
                            pass
                            # in case there is none object in maxSpot    
                    
                    currentGroups.append( myGroup )
                    currentAlive += 1

        # 与上一帧比较

        global totalAlive
        global z
        # 首先提取上一帧存活的不同动作类的代表像素
        if totalAlive == 0 : # 上一帧不存在"存活的"动作类
            if currentAlive != 0 :
                totalAlive = len(currentGroups)
                for group in currentGroups :
                    touchGroups.append(movementGroup(group, z + 1))
                    z += 1
                
        else: # 找出上一帧存活的动作类，并存入previousGroups
            for tg in touchGroups:
                if tg.alive == True :
                    previousGroups.append(spotGroupAssist( tg.groupArrayList[len(tg.groupArrayList) - 1].mainSpot , tg ) )
                
            
            # 将此帧像素与上一帧像素对应，并加入相应touchGroup
            if currentAlive == 0 :
                totalAlive = 0
                for tg in touchGroups:
                    tg.alive = False
                    tg.end = tg.start + len(tg.groupArrayList)
                # analyzeGroups( touchGroups )
                # touchGroups.clear()
            
            else:
                match( currentGroups, previousGroups )
                totalAlive = currentAlive



    
# Test Function

def test_ground_truth() -> bool:

    global touchGroups
    global mydrawStep
    touchGroups = []
    now_time = int(round(tm.time() * 1000))
    for control_index in range(0, 10):
        spot = [] # list of spotgroup
        updateTrack(getFuse(getPressDownDouble()), spot, now_time)   
        now_time += 100
    for control_index in range(0, 8):
        spot = []
        updateTrack(getFuse(getPressMiddleSingle()), spot, now_time)
        now_time += 200
    for control_index in range(0, 10):
        spot = []  
        updateTrack(getFuse(getPressDownDouble()), spot, now_time)  
        now_time += 100

    mydrawStep += 1
    # print(analyzeGroups(touchGroups))
    return analyzeGroups(touchGroups)

def test_lll() -> bool:

    global touchGroups
    global mydrawStep
    touchGroups = []
    now_time = int(round(tm.time() * 1000))
    for control_index in range(0, 10):
        spot = [] # list of spotgroup
        updateTrack(getFuse(getPressUpDouble()), spot, now_time)   
        now_time += 500
    for control_index in range(0, 8):
        spot = []
        updateTrack(getFuse(getPressMiddleSingle()), spot, now_time)
        now_time += 30
    for control_index in range(0, 10):
        spot = []  
        updateTrack(getFuse(getPressUpDouble()), spot, now_time)  
        now_time += 500

    mydrawStep += 1
    # print(analyzeGroups(touchGroups))
    return analyzeGroups(touchGroups)

def test_lsl() -> bool:

    global touchGroups
    global mydrawStep
    touchGroups = []
    now_time = int(round(tm.time() * 1000))
    for control_index in range(0, 10):
        spot = [] # list of spotgroup
        updateTrack(getFuse(getPressUpDouble()), spot, now_time)   
        now_time += 150
    for control_index in range(0, 8):
        spot = []
        updateTrack(getFuse(getPressMiddleSingle()), spot, now_time)
        now_time += 50
    for control_index in range(0, 10):
        spot = []  
        updateTrack(getFuse(getPressUpDouble()), spot, now_time)  
        now_time += 150

    mydrawStep += 1
    # print(analyzeGroups(touchGroups))
    return analyzeGroups(touchGroups)

def test_near_2_0() -> bool:
    
    global touchGroups
    global mydrawStep
    touchGroups = []
    now_time = int(round(tm.time() * 1000))
    for control_index in range(0, 10):
        spot = [] # list of spotgroup
        updateTrack(getFuse(getPressUpDouble()), spot, now_time)   
        now_time += 30
    for control_index in range(0, 8):
        spot = []
        updateTrack(getFuse(getPressMiddleSingle()), spot, now_time)
        now_time += 400
    for control_index in range(0, 10):
        spot = []  
        updateTrack(getFuse(getPressUpDouble()), spot, now_time)  
        now_time += 30

    mydrawStep += 1
    # print(analyzeGroups(touchGroups))
    return analyzeGroups(touchGroups)

def test_near_1_0() -> bool:
    
    global touchGroups
    global mydrawStep
    touchGroups = []
    now_time = int(round(tm.time() * 1000))
    for control_index in range(0, 10):
        spot = [] # list of spotgroup
        updateTrack(getFuse(getPressUpDouble()), spot, now_time)   
        now_time += 50
    for control_index in range(0, 8):
        spot = []
        updateTrack(getFuse(getPressMiddleSingle()), spot, now_time)
        now_time += 300
    for control_index in range(0, 10):
        spot = []  
        updateTrack(getFuse(getPressUpDouble()), spot, now_time)  
        now_time += 50

    mydrawStep += 1
    # print(analyzeGroups(touchGroups))
    return analyzeGroups(touchGroups)

def test_near_0_5() -> bool:
    
    global touchGroups
    global mydrawStep
    touchGroups = []
    now_time = int(round(tm.time() * 1000))
    for control_index in range(0, 10):
        spot = [] # list of spotgroup
        updateTrack(getFuse(getPressUpDouble()), spot, now_time)   
        now_time += 50
    for control_index in range(0, 8):
        spot = []
        updateTrack(getFuse(getPressMiddleSingle()), spot, now_time)
        now_time += 250
    for control_index in range(0, 10):
        spot = []  
        updateTrack(getFuse(getPressUpDouble()), spot, now_time)  
        now_time += 50

    mydrawStep += 1
    # print(analyzeGroups(touchGroups))
    return analyzeGroups(touchGroups)

def test_near_0_3() -> bool:
    
    global touchGroups
    global mydrawStep
    touchGroups = []
    now_time = int(round(tm.time() * 1000))
    for control_index in range(0, 10):
        spot = [] # list of spotgroup
        updateTrack(getFuse(getPressUpDouble()), spot, now_time)   
        now_time += 70
    for control_index in range(0, 8):
        spot = []
        updateTrack(getFuse(getPressMiddleSingle()), spot, now_time)
        now_time += 230
    for control_index in range(0, 10):
        spot = []  
        updateTrack(getFuse(getPressUpDouble()), spot, now_time)  
        now_time += 70

    mydrawStep += 1
    # print(analyzeGroups(touchGroups))
    return analyzeGroups(touchGroups)
        
def test_near_0_2() -> bool:
    
    global touchGroups
    global mydrawStep
    touchGroups = []
    now_time = int(round(tm.time() * 1000))
    for control_index in range(0, 10):
        spot = [] # list of spotgroup
        updateTrack(getFuse(getPressUpDouble()), spot, now_time)   
        now_time += 80
    for control_index in range(0, 8):
        spot = []
        updateTrack(getFuse(getPressMiddleSingle()), spot, now_time)
        now_time += 220
    for control_index in range(0, 10):
        spot = []  
        updateTrack(getFuse(getPressUpDouble()), spot, now_time)  
        now_time += 80

    mydrawStep += 1
    # print(analyzeGroups(touchGroups))
    return analyzeGroups(touchGroups)

def test_near_0_1() -> bool:
    
    global touchGroups
    global mydrawStep
    touchGroups = []
    now_time = int(round(tm.time() * 1000))
    for control_index in range(0, 10):
        spot = [] # list of spotgroup
        updateTrack(getFuse(getPressUpDouble()), spot, now_time)  
        now_time += 90
    for control_index in range(0, 8):
        spot = []
        updateTrack(getFuse(getPressMiddleSingle()), spot, now_time)
        now_time += 210
    for control_index in range(0, 10):
        spot = []  
        updateTrack(getFuse(getPressUpDouble()), spot, now_time)  
        now_time += 90

    mydrawStep += 1
    # print(analyzeGroups(touchGroups))
    return analyzeGroups(touchGroups)

# Main Function

test_ground_truth()
total_cnt_0_1 = 0
total_cnt_0_2 = 0
total_cnt_0_3 = 0
total_cnt_0_5 = 0
total_cnt_1_0 = 0
total_cnt_2_0 = 0
total_cnt_ground = 0
for epoch in range(0, 200):
    if test_near_0_1() == True:
        total_cnt_0_1 += 1
    if test_near_0_2() == True:
        total_cnt_0_2 += 1
    if test_near_0_3() == True:
        total_cnt_0_3 += 1
    if test_near_0_5() == True:
        total_cnt_0_5 += 1
    if test_near_1_0() == True:
        total_cnt_1_0 += 1
    if test_near_2_0() == True:
        total_cnt_2_0 += 1
    if test_ground_truth() == True:
        total_cnt_ground += 1

print("------------------- total score ------------------")
print("---" + str(total_cnt_ground) + " " + str(total_cnt_0_1) + " " + str(total_cnt_0_2) + " " + str(total_cnt_0_3) + " " + str(total_cnt_0_5) + " " + str(total_cnt_1_0) + " " + str(total_cnt_2_0) + "---")
print("--------------------------------------------------")
