//
//  HenryMilestoneDetailViewController.h
//  Henry
//
//  Created by Grove, Carter J on 10/19/14.
//  Copyright (c) 2014 Rose-Hulman. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface HenryMilestoneDetailViewController : UIViewController
@property NSString *ProjectID;
@property NSString *MileStoneID;
@property NSString *milestoneName;
@property (weak, nonatomic) IBOutlet UILabel *milestoneNameLabel;
@property (weak, nonatomic) IBOutlet UILabel *dueDateLabel;
@property (weak, nonatomic) IBOutlet UITextView *descriptionView;
@property (weak, nonatomic) IBOutlet UILabel *tasksCompletedLabel;
@property (strong, nonatomic) IBOutlet UIProgressView *tasksCompleteBar;
@property NSArray *userTasks;
@property NSString *uid;
@end