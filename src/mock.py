from typing import List
import numpy
from constant import GAUSS_NOISE

def getPressDownDouble() -> list:
    '''
    return a random 36 * 18 matrix with 1000 on the both side
    '''
    ret = [[0 for _row in range(0, 18)] for _col in range(0, 36)]
    ret[33][17] = 1000
    ret[34][17] = 1000
    ret[35][17] = 1000
    ret[33][0] = 1000
    ret[34][0] = 1000
    ret[35][0] = 1000
    return ret

def getPressDownSingle() -> list:
    '''
    return a random 36 * 18 matrix with 1000 on the right side
    '''
    ret = [[0 for _row in range(0, 18)] for _col in range(0, 36)]
    ret[17][17] = 1000
    ret[18][17] = 1000
    ret[19][17] = 1000
    return ret

def getPressMiddleDouble() -> list:
    '''
    return a random 36 * 18 matrix with 1000 on the both side
    '''
    ret = [[0 for _row in range(0, 18)] for _col in range(0, 36)]
    ret[17][17] = 1000
    ret[18][17] = 1000
    ret[19][17] = 1000
    ret[17][0] = 1000
    ret[18][0] = 1000
    ret[19][0] = 1000
    return ret

def getPressMiddleSingle() -> list:
    '''
    return a random 36 * 18 matrix with 1000 on the right side
    '''
    ret = [[0 for _row in range(0, 18)] for _col in range(0, 36)]
    ret[17][17] = 1000
    ret[18][17] = 1000
    ret[19][17] = 1000
    return ret

def getPressUpDouble() -> list:
    '''
    return a random 36 * 18 matrix with 1000 on the both side
    '''
    ret = [[0 for _row in range(0, 18)] for _col in range(0, 36)]
    ret[0][17] = 1000
    ret[1][17] = 1000
    ret[2][17] = 1000
    ret[0][0] = 1000
    ret[1][0] = 1000
    ret[2][0] = 1000
    return ret

def getPressUpSingle() -> list:
    '''
    return a random 36 * 18 matrix with 1000 on the right side
    '''
    ret = [[0 for _row in range(0, 18)] for _col in range(0, 36)]
    ret[0][17] = 1000
    ret[1][17] = 1000
    ret[2][17] = 1000
    return ret

def getFuse(input_matrix: List[List[int]]) -> list:
    '''
    get gaussian fuse for list of list of int
    '''
    ans_matrix = list(numpy.random.normal(loc = 0.0, scale = GAUSS_NOISE, size = 36 * 18).reshape(36, 18))
    for row in range(0, len(ans_matrix)):
        for col in range(0, len(ans_matrix[0])):
            input_matrix[row][col] += abs(ans_matrix[row][col])
    return input_matrix


# getFuse([[0 for i in range(0, 18)] for j in range(0, 36)])

