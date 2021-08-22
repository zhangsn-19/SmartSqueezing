import torch 
import numpy as np
import torch.nn as nn 
from torch.nn import CrossEntropyLoss, MSELoss
from torch.optim import SGD
from constant import EPOCHS, SIZE, MAXLEN
from util import padding, float_
import random 
import ipdb

'''
    read from preprocessing
'''
X_in_true = []
X_in_false = []
X = []

def get_x_true_data():
    global X_in_true
    with open("../processed_data/2class/true.txt", "r") as in_f:
        for line in in_f:
            if str(line).startswith("touch") and len(line) > 8:
                X_in_true.append(( [eval(_) for _ in line[8 : -1]], True))
    
def get_x_false_data():
    global X_in_false
    with open("../processed_data/2class/false.txt", "r") as in_f:
        for line in in_f:
            if str(line).startswith("touch") and len(line) > 8:
                X_in_false.append(( [eval(_) for _ in line[8 : -1]], False))

def shuffle():
    global X 
    global X_in_true 
    global X_in_false
    X += X_in_true
    X += X_in_false 
    random.shuffle(X) 

class MLP(nn.Module):
        def __init__(self, input_size, output_size):
            super(MLP, self).__init__()
            self.linear = nn.Sequential(
                nn.Linear(input_size, input_size // 2),
                nn.ReLU(inplace=True),
                nn.Linear(input_size // 2, input_size // 4),
                nn.ReLU(inplace=True),
                nn.Linear(input_size // 4, output_size)
            )
 
        def forward(self, x):
            out = self.linear(x)
            return out

model = MLP(input_size = MAXLEN, output_size = 1)
loss = MSELoss()
optimizer = SGD(model.parameters(), lr = 0.01)

def train():
    for epoch in range(0, EPOCHS):
        random.shuffle(X)
        for cnt, x in enumerate(X):
            # print(x[0])
            y_predict = model(torch.from_numpy( np.array( float_(padding(x[0], MAXLEN) ) ) ).to(torch.float32) )
            if x[1] == True:
                y_in = 1
            else:
                y_in = 0
            y_truth = torch.unsqueeze(torch.from_numpy( np.array(float(y_in))).to(torch.float32), 0)
            # ipdb.set_trace()
            y_loss = loss(y_predict, y_truth)
            optimizer.zero_grad()
            y_loss.backward()
            
            print(str(y_loss))

get_x_true_data()
get_x_false_data()
shuffle()
train()