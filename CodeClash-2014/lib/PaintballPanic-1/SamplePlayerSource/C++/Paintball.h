/*
 ** Useful constants
 */
#ifndef __PAINTBALL_H__
#define __PAINTBALL_H__

/**
 * The Red player.
 */
const int RED = 0;

/**
 * The Blue player.
 */
const int BLUE = 1;

/**
 * The number of children on a single team.
 */
const int NUM_CHILDREN_PER_TEAM = 4;

/**
 * The number of spaces on one of the dimensions of the square field.
 */
const int FIELD_DIMENSION = 31;

/**
 * Field encodings as integers.
 */
const int EMPTY               = 0;
const int TREE                = 1;
const int LOW_WALL            = 2;
const int HIGH_WALL           = 3;
const int RED_FLAG            = 4;
const int BLUE_FLAG           = 5;
const int ADAPTER             = 6;
const int SHIELD              = 7;
const int BASIC_LAUNCHER      = 8;
const int RAPID_FIRE_LAUNCHER = 9;

/**
 * Number of actions
 */
const int NUM_ACTIONS = 10;

/**
 * The maximum euclidean distance a paintball can travel when launched.
 */
const int MAX_LAUNCH_DISTANCE = 24;

/**
 * Inventory encodings.
 */
const int NOTHING                        = 0;
const int ONE_BASIC_LAUNCHER             = 1;
const int ONE_SHIELD                     = 2;
const int SHIELD_AND_BASIC_LAUNCHER      = 3;
const int ONE_RAPID_FIRE_LAUNCHER        = 4;
const int SHIELD_AND_RAPID_FIRE_LAUNCHER = 5;

#endif
