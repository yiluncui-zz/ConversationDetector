import sys
import nltk
import struct 
import re

donts = (re.compile(r'^(dont|dun)$',re.IGNORECASE), 'do not')
didnts = (re.compile(r'^didnt$',re.IGNORECASE), 'did not')
doesnts = (re.compile(r'^doesnt$',re.IGNORECASE), 'does not')
dunnos = (re.compile(r'^(dunno|donno)$', re.IGNORECASE), 'do not know')
ims = (re.compile(r'^(im|Im)$'), 'I am')
its = (re.compile(r'^its$',re.IGNORECASE), 'it is')
thats = (re.compile(r'^thats$',re.IGNORECASE), 'that is')
whats = (re.compile(r'^whats$',re.IGNORECASE), 'what is')
dotsIdentifier = re.compile('.*\.\.+')
dots = re.compile(r'\.\.+')

replacements = [donts, didnts, doesnts, dunnos, ims, its, thats, whats]


def tupleCompare(a,b):
    return a[0]-b[0]

def genDataSection(formatter,tokens):
    # Data section 
    s = '\n@DATA\n' 
    for (tok, label) in tokens:
        s += "{ "
        indexToValues = []
        for i in range(len(formatter._features)):
            fname = formatter._features[i][0]
            if tok.get(fname) is not None:
                indexToValues.append((i,formatter._fmt_arff_val(tok.get(fname))))
        indexToValues.append((len(formatter._features),formatter._fmt_arff_val(label)))
        indexToValues = sorted(indexToValues,cmp=tupleCompare)
        for i in range(len(indexToValues)): indexToValues[i] = "%i %s" % indexToValues[i]
        s += ', '.join(indexToValues)
        s += "}\n"
    return s 

def increment(features, feature):
    if feature in features.keys():
        features[feature]+=1
    else: features[feature]=1

def genBeginsAndEndsWith(posTags, features):
    if len(posTags) == 0: return
    increment(features,'begins(%s)' % posTags[0][1])
    increment(features,'ends(%s)' % posTags[len(posTags)-1][1])

def genFeatureWords(features,word):
    if re.match(r'.*any[a-zA-Z1]*.*',word) is not None: increment(features,'contains(any)')

    #whQuestions
    elif re.match(r'[^a-zA-Z]*w[ahu]t.*',word) is not None: increment(features,'contains(what)')

    #yAnswer
    elif re.match(r'.*y[ea]+[a-zA-Z]+.*',word) is not None: increment(features,'contains(ye)')
    elif re.match(r'.*yup.*',word) is not None: increment(features,'contains(yup)')
    elif re.match(r'.*ok.*',word) is not None: increment(features,'contains(ok)')

    #Reject
    elif re.match(r'.*right.*',word) is not None: increment(features,'contains(right)')

    #nAnswer
    elif re.match(r'.*n[\'o]t.*',word) is not None: increment(features,'contains(not)')
    elif re.match(r'[^a-zA-Z]*n[oa]+.*',word) is not None: increment(features,'contains(no)')

    #Reject
    elif re.match(r'.*nope.*',word) is not None: increment(features,'contains(nope)')
    
    #Greet
    elif re.match(r'.+llo.*',word) is not None: increment(features,'contains(llo)')
    elif re.match(r'[^a-zA-Z]*hey.*',word) is not None: increment(features,'contains(hey)')
    elif re.match(r'[^a-zA-Z]*hi.*',word) is not None: increment(features,'contains(hi)')
    elif re.match(r'.*morning.*',word) is not None: increment(features,'contains(morning)')
    elif re.match(r'[a-zA-Z]*up.*',word) is not None: increment(features,'contains(up)')
    elif re.match(r'[^a-zA-Z]*yo.*',word) is not None: increment(features,'contains(yo)')

    elif re.match(r'[a-zA-Z]*bye.*',word) is not None: increment(features,'contains(bye)')
    elif re.match(r'.*g[t2]g.*',word) is not None: increment(features,'contains(g2g)')
    elif re.match(r'.*(afk|brb|away).*',word) is not None: increment(features,'contains(brb)')
    elif re.match(r'[^a-zA-Z]*[a-zA-Z]+ya.*',word) is not None: increment(features,'contains(cya)')
    elif re.match(r'[^a-zA-Z]*(later|ltr|l8ter|l8r).*',word) is not None: increment(features,'contains(later)')
    elif re.match(r'.*(night|nite).*',word) is not None: increment(features,'contains(night)')

def genUnigramsBigrams(posTags, features):
    for i in range(len(posTags)-1):
        genFeatureWords(features,posTags[i][0].lower())
        posTagF = posTags[i][1]
        posTagB = posTags[i+1][1]
        increment(features,'unigram(%s)' % (posTagF))
        if i == len(posTags)-2: increment(features,'unigram(%s)' % posTagB)
        increment(features,'bigram(%s)' % (posTagF+'-'+posTagB))

def genOthers(post,features):
    if '?' in post: increment(features, 'contains(?)')
    if '..' in post: increment(features,'contains(..)')
    if '*' in post: increment(features,'contains(*)')

def fixDots(tokens):
    newTokens = []
    for token in tokens:
        if dotsIdentifier.match(token) is not None:
            if token == '...':
                newTokens.append(token)
                continue
            newToks = dots.split(token)
            for i in range(len(newToks)):
                if newToks[i] == '': newTokens.append('...')
                else:
                    newTokens.append(newToks[i])
                    if i != len(newToks)-1 :newTokens.append('...')
        else:
            newTokens.append(token)
    return newTokens

def fixContractions(token,newTokens):
    replaced = False
    for replacement in replacements:
        if replacement[0].match(token) is not None:
            tokenizedReplacements = replacement[0].sub(replacement[1],token).split(' ')
            for tokReplacement in tokenizedReplacements:
                if tokReplacement != '':
                    newTokens.append(tokReplacement)
            replaced = True
    if replaced == False : newTokens.append(token)
    
def dialogue_act_features(post):
    features = {}
    tokens = nltk.word_tokenize(post)
    tokens = fixDots(tokens)
    newTokens = []
    for token in tokens:
        fixContractions(token,newTokens)
    tokens = newTokens
    posTags = nltk.pos_tag(tokens)
    if posTags[len(posTags)-1][1]=='.': posTags=posTags[:len(posTags)-1]
    genOthers(post,features)
    genBeginsAndEndsWith(posTags,features)
    genUnigramsBigrams(posTags,features)
    return features

def toArff(featureSets):
    formatter = nltk.classify.weka.ARFF_Formatter.from_train(featureSets)
    print formatter.header_section()
    print genDataSection(formatter,featureSets)

if __name__=="__main__":
    posts = nltk.corpus.nps_chat.xml_posts()
    
    if len(sys.argv) == 3:
        posts = [post for post in posts if post.get('class') == sys.argv[2]]
    else:
        posts = [post for post in posts if post.get('class') != 'System']
    featureSets = [(dialogue_act_features(post.text), post.get("class")) 
                   for post in posts]

    toArff(featureSets)
    exit(0)

    size = int(len(featureSets)*float(sys.argv[1]))
    trainSet, testSet = featureSets[size:], featureSets[:size]

    classifier = nltk.NaiveBayesClassifier.train(trainSet)
    predictions = []
    gold = []
    for i in range(len(testSet)):
        case = testSet[i]
        predictions.append(classifier.classify(case[0]))
        gold.append(case[1])
        #if case[1] == "Accept": print posts[i].text
    cm = nltk.ConfusionMatrix(gold,predictions)
    print cm.pp()
    print nltk.classify.accuracy(classifier,testSet)
