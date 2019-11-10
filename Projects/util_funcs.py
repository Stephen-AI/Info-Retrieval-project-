def compare_files(fileA, fileB):
    fiA = open(fileA, 'r')
    fiB = open(fileB, 'r')

    la = fiA.readline()
    lb = fiB.readline()
    lineno = 1
    while(len(la.strip())):
        if (la != lb):
            print("diff on line {}: {} vs {}".format(lineno, la, lb))
            break
        la = fiA.readline()
        lb = fiB.readline()
        lineno += 1
    fiA.close()
    fiB.close()

if __name__ == "__main__":
    compare_files('ta_trace.txt', 'my_trace.txt')

    