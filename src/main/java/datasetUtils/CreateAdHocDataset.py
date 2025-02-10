# CREATES perfect.csv

from sklearn.datasets import make_blobs
import math
import random
import numpy

def create_centers():
    X, y, centers = make_blobs(n_samples=100000, n_features=15, centers=21, cluster_std=0.5, center_box=(0, 50), random_state=0, return_centers=True)
    return centers

def find_dist(sample, center):
    dist = 0
    for i in range(len(sample)):
        dist += (sample[i]-center[i])*(sample[i]-center[i])
    return math.sqrt(dist)

# Find the radius:
def find_radius(X, y, centers):
    radius = 0
    for i in range(len(X)):
        dist = find_dist(X[i], centers[y[i]])
        if (dist > radius):
            radius = dist
    return radius

# Write dataset to output file:
def create(X, y):
    with open('../../../../data/randomized/perfect_dataset.csv', 'w') as f:
        print("Started writing")

        # The first 21+42 points are special
        specials = []
        for i in range(21+42):
            specials.append(i)
        random.shuffle(specials)
        
        j = 63
        kk = 0
        zzzz = 0
        for i in range(len(X)-63): 
            if (j > 1000 and j % 140 == 0 and kk < 63):
                sample = specials[kk]
                kk += 1
            else:
                sample = j
            if (sample < 63):
                zzzz += 1
                print(sample)
            for z in X[sample]:
                f.write(str(z)+";")
            f.write(str(y[sample]))
            f.write(";\n")
            j += 1
        print("Finished writing")
        print(zzzz)
    

radius = 10
centers = create_centers()
min_dist = 100**15
for i in range(21):
    for j in range(i):
        if (i == j):
            continue
        dist = find_dist(centers[i], centers[j])
        min_dist = min(min_dist, dist)

print(min_dist)
if (min_dist < radius):
    raise Exception("Radius too high")

X = []
y = []
for i in range(21):
    X.append(centers[i])
    y.append(i%7)

for j in range(42):
    sample = centers[j%21]
    if j < 21:
        sample[0] = sample[0]-radius
    else:
        sample[0] = sample[0]+radius
    X.append(sample)
    y.append(random.randint(0, 6))

print(len(X))

bad = 0
centers_corr = numpy.zeros(len(centers)*7)
while len(X) < 100000:
    #Select a center
    i = random.randint(0, 20)
    corr_center = centers[i]
    sample = []
    for j in range(15):
        sample.append(corr_center[j]+random.gauss(0, math.sqrt(radius/2)))
    if (find_dist(corr_center, sample) > radius):
        bad += 1
        continue
    col = random.randint(0, 6)
    centers_corr[i*7+col] += 1
    X.append(sample)
    y.append(col)

print(centers_corr) # See if the centers are well distributed
print(bad) # See how many bad samples we missed

create(X,y)