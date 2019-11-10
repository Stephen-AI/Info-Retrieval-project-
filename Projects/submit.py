import argparse, os, sys, shutil
from zipfile import ZipFile

def find(names: set, path: str):
    result = []
    for root, dirs, files in os.walk(path):
        found = set()
        for name in names:
            java = name.strip() + '.java'
            if java in files:
                result.append(os.path.join(root, java))
                found.add(name)
        names = names - found
    return result

def zip_submission(zip_name, files):
    with ZipFile(zip_name, 'w') as zip:
        for file in files:
            className = file[:-4] + 'class'
            zip.write(file)
            zip.write(className)
        
if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="make submitting code easier")
    parser._action_groups.pop()
    required = parser.add_argument_group('required arguments')
    required.add_argument('-eid', required=True)
    required.add_argument('-projnum', required=True)
    required.add_argument('-classlist', required=True, help="The list of classes modified or created")
    args = parser.parse_args()
    zip_name = 'proj{}_{}_code.zip'.format(args.projnum, args.eid)
    files = set()
    with open(args.classlist, 'r') as classlist:
        for line in classlist:
            files.update(line.split(','))
    #find target files
    java_files = find(files, 'ir')
    #compile 'em
    for java in java_files:
        os.system('javac {}'.format(java))
    #zip 'em up
    zip_submission(zip_name, java_files)
    #run it
    
    


    
