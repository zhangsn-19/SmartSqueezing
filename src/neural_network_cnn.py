import numpy as np
import keras
from keras.models import Sequential
from keras.layers import Dense, Dropout, Flatten
from keras.layers import Conv2D, MaxPooling2D
from keras.optimizers import SGD
# 这一行新加的，用于导入绘图包
from keras.utils import plot_model
# 生成数据
# 生成100张图片，每张图片100*100大小，是3通道的。
# x_train = [getPressDownDouble()]
x_train = np.random.random((100, 100, 100, 3))
y_train = keras.utils.to_categorical(np.random.randint(10, size=(100, 1)), num_classes=10)
x_test = np.random.random((20, 100, 100, 3))
y_test = keras.utils.to_categorical(np.random.randint(10, size=(20, 1)), num_classes=10)

model = Sequential()
# 一层卷积层，包含了32个卷积核，大小为3*3
model.add(Conv2D(32, (3, 3), activation='relu', input_shape=(100, 100, 3)))
model.add(Conv2D(32, (3, 3), activation='relu'))
# 一个最大池化层，池化大小为2*2
model.add(MaxPooling2D(pool_size=(2, 2)))
# 遗忘层，遗忘速率为0.25
model.add(Dropout(0.25))
# 添加一个卷积层，包含64个卷积和，每个卷积和仍为3*3
model.add(Conv2D(64, (3, 3), activation='relu'))
model.add(Conv2D(64, (3, 3), activation='relu'))
# 来一个池化层
model.add(MaxPooling2D(pool_size=(2, 2)))
model.add(Dropout(0.25))
# 压平层
model.add(Flatten())
# 来一个全连接层
model.add(Dense(256, activation='relu'))
# 来一个遗忘层
model.add(Dropout(0.5))
# 最后为分类层
model.add(Dense(10, activation='softmax'))

sgd = SGD(lr=0.01, decay=1e-6, momentum=0.9, nesterov=True)
model.compile(loss='categorical_crossentropy', optimizer=sgd)

model.fit(x_train, y_train, batch_size=32, epochs=10)
score = model.evaluate(x_test, y_test, batch_size=32)
#这一行新加的，用于绘图
plot_model(model, to_file='modelcnn.png',show_shapes=True)
model.save("CNN.model")
