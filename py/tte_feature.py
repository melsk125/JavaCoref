import sys
import codecs
import re

from nltk import pos_tag, PorterStemmer

s = PorterStemmer()


def get_string(string_list, offset_list, idd, name):
	if idd + offset_list[0] < 0 or idd + offset_list[-1] >= len(string_list):
		return None
	s = ""
	
	s += "%s[%d]" % (name, offset_list[0])
	for i in offset_list[1:]:
		s += "|%s[%d]" % (name, i)
	s += "="

	#print string_list
	s += "%s" % string_list[idd + offset_list[0]]
	for i in offset_list[1:]:
		s += "|%s" % string_list[idd + i]

	return s

def shape_features(sentence, token_id):
	word = sentence[token_id]

	# lower-cased
	features = "p[01]=%s" % word.lower()

	# prefixes 3-5
	features += "\tp[02]=%s,%s,%s" % (word[:3], word[:4], word[:5])

	# suffix 3-5
	features += "\tp[03]=%s,%s,%s" % (word[-3:], word[-4:], word[-5:])

	# stem
	features += "\tp[04]=%s" % s.stem(word)

	# is_pair_of_digits
	if re.search(r"^\d{2}$", word):
		features += "\tp[05]"

	# is_four_digits
	if re.search(r"^\d{4}$", word):
		features += "\tp[06]"

	# letters and digits
	if re.search(r"^[a-zA-Z0-9]+$", word) and re.search(r"[a-zA-Z]", word) and re.search(r"\d", word):
		features += "\tp[07]"

	# digits and hyphens
	if re.search(r"^[0-9\-]+$", word) and re.search(r"\d", word) and re.search(r"\-", word):
		features += "\tp[08]"

	# digits and slashes
	if re.search(r"^[0-9/]+$", word) and re.search(r"\d", word) and re.search(r"/", word):
		features += "\tp[09]"

	# digits and commas
	if re.search(r"^[0-9,]+$", word) and re.search(r"\d", word) and re.search(r",", word):
		features += "\tp[10]"

	# digits and dots
	if re.search(r"^[0-9\.]+$", word) and re.search(r"\d", word) and re.search(r"\.", word):
		features += "\tp[11]"

	# uppercase and dots
	if re.search(r"^[A-Z\.]+$", word) and re.search(r"[A-Z]", word) and re.search(r"\.", word):
		features += "\tp[12]"

	# initial upper-case
	if re.search(r"^[A-Z][A-Za-z]*$", word):
		features += "\tp[13]"

	# only upper-case
	if re.search(r"^[A-Z]+$", word):
		features += "\tp[14]"

	# only lower-case
	if re.search(r"^[a-z]+$", word):
		features += "\tp[15]"

	# only digits
	if re.search(r"^\d+$", word):
		features += "\tp[16]"

	# only non alnum
	if re.search(r"[^a-zA-Z0-9]+$", word):
		features += "\tp[17]"

	# contains upper-case
	if re.search(r"[A-Z]", word):
		features += "\tp[18]"

	# contains lower-case
	if re.search(r"[a-z]", word):
		features += "\tp[19]"

	# contains digits
	if re.search(r"\d", word):
		features += "\tp[20]"

	# contains non alnum
	if re.search(r"[^A-Za-z0-9]", word):
		features += "\tp[21]"

	# date
	if re.search(r"^(19|20)\d\d[- /.](0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])$", word):
		features += "\tp[22]"

	# pattern
	pat = ""
	for c in word:
		if re.search(r"[0-9]", c):
			pat += "0"
		elif re.search(r"[a-z]", c):
			pat += "a"
		elif re.search(r"[A-Z]", c):
			pat += "A"
		else:
			pat += c
	features += "\tp[23]=%s" % pat

	col = ""
	for c in pat:
		if len(col) == 0:
			col += c
			continue
		if c != col[-1]:
			col += c
	features += "\tp[24]=%s" % col

	return features

def get_features(sentence, pos_list, token_id):
	sentence_length = len(sentence)

	features = ""

	for i in range(-2,3):
		result =  get_string(sentence, [i], token_id, "w")
		if result:
			features += "\t%s" % result

	for i in range(-1,1):
		result = get_string(sentence, range(i,i+2), token_id, "w")
		if result:
			features += "\t%s" % result

	for i in range(-2,3):
		result =  get_string(pos_list, [i], token_id, "pos")
		if result:
			features += "\t%s" % result

	for i in range(-2,2):
		result = get_string(pos_list, range(i,i+2), token_id, "pos")
		if result:
			features += "\t%s" % result

	for i in range(-2,1):
		result = get_string(pos_list, range(i,i+3), token_id, "pos")
		if result:
			features += "\t%s" % result

	features += "\t%s" % shape_features(sentence, token_id)

	return features

def txt_to_token(txt):
	sentences = []
	bio = []
	current_sentence = []
	current_bio = []
	for line in txt.split("\n"):
		line = line.strip()
		if line == "":
			if len(current_sentence) == 0: continue
			sentences.append(current_sentence)
			bio.append(current_bio)
			current_sentence = []
			current_bio = []
			continue
		line = line.split("\t")
		current_bio.append(line[0])
		current_sentence.append(line[1])
	if len(current_sentence) > 0:
		sentences.append(current_sentence)
		bio.append(current_bio)
	return (sentences, bio)

if __name__ == "__main__":
	txt = sys.stdin.read()
	(sents, bio) = txt_to_token(txt)
	doc_length = len(sents)
	for sent_id in range(doc_length):
		sent = sents[sent_id]
		pos_list = pos_tag(sent)
		pos_list = [e[1] for e in pos_list]
		sent_length = len(sent)
		for token_id in range(sent_length):
			line = bio[sent_id][token_id]
			line += get_features(sent, pos_list, token_id)
			print "%s" % line
		print
	
