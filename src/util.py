from constant import INT_INF
def getNumofCommonSubstr(str1: str, str2: str):
    '''
    return str and int
    '''
 
    l_str1 = len(str1)
    l_str2 = len(str2)
    record = [[0 for i in range(l_str2 + 1)] for j in range(l_str1 + 1)]  # 多一位
    maxNum = 0          # 最长匹配长度
    p = 0               # 匹配的起始位
 
    for i in range(l_str1):
        for j in range(l_str2):
            if str1[i] == str2[j]:
                # 相同则累加
                record[i + 1][j + 1] = record[i][j] + 1
                if record[i + 1][j + 1] > maxNum:
                    # 获取最大匹配长度
                    maxNum = record[i + 1][j + 1]
                    # 记录最大匹配长度的终止位置
                    p = i + 1
                    #print(record)
    return str1[p - maxNum : p], maxNum

def max_hamming(a, b):
    '''
    a is the ground truth and b is to be tested
    '''
    min_len = INT_INF
    if len(a) > len(b):
        for i in range(0, len(a) - len(b)):
            now_len = hamming(a[i: len(b) + i], b)
            if now_len < min_len:
                min_len = now_len 
    elif len(a) < len(b):
        for i in range(0, len(b) - len(a)):
            now_len = hamming(a, b[i: len(a) + i])
            if now_len < min_len:
                min_len = now_len 
    else:
        now_len = hamming(a, b)
        if now_len < min_len:
            min_len = now_len   

    return min_len

def hamming(a, b) -> int:
    '''
    compute and return the Hamming distance between the integers
    '''
    return bin(int(a) ^ int(b)).count("1")

