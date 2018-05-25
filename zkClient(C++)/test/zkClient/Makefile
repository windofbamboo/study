#COMAKE2 edit-mode: -*- Makefile -*-
####################64Bit Mode####################
ifeq ($(shell uname -m),x86_64)
CC=gcc
CXX=g++
CXXFLAGS=-g -pipe -W -Wall -fPIC
CFLAGS=-g -pipe -W -Wall -fPIC
CPPFLAGS=-D_GNU_SOURCE -D__STDC_LIMIT_MACROS -DVERSION=\"1.9.8.7\"

INCPATH=-I. \
				-I/usr/include \
				-I/home/roam/include/zookeeper \
				-I/home/roam/include/rapidjson \
				-I/home/roam/include/roam/frame

ResInfoZkClientTest:ResInfoZkClientTest.o
	$(CXX) ResInfoZkClientTest.o -Xlinker "-("  /home/roam/lib/zookeeper/libzookeeper_mt.a \
	/home/roam/lib/zookeeper/libzookeeper_st.a \
	/home/roam/lib/roam/libroam_frame.a \
	-lpthread \
	-lrt -Xlinker "-)" -o test

ResInfoZkClientTest.o:ResInfoZkClientTest.cc
	$(CXX) -c $(INCPATH) $(CPPFLAGS) $(CXXFLAGS) -g -o ResInfoZkClientTest.o ResInfoZkClientTest.cc



endif #ifeq ($(shell uname -m),x86_64)