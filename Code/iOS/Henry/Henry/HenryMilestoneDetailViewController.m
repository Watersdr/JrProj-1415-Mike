//
//  HenryMilestoneDetailViewController.m
//  Henry
//
//  Created by Grove, Carter J on 10/19/14.
//  Copyright (c) 2014 Rose-Hulman. All rights reserved.
//

#import "HenryMilestoneDetailViewController.h"
#import "HenryTasksTableViewController.h"
#import "HenryFirebase.h"

@interface HenryMilestoneDetailViewController ()
@property Firebase *fb;
@end

@implementation HenryMilestoneDetailViewController

@synthesize pieChart;

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.fb = [HenryFirebase getFirebaseObject];
    [UIApplication sharedApplication].networkActivityIndicatorVisible = YES;
    
    // Attach a block to read the data at our posts reference
    [self.fb observeEventType:FEventTypeValue withBlock:^(FDataSnapshot *snapshot) {
        [self updateInfo: snapshot];
    } withCancelBlock:^(NSError *error) {
        NSLog(@"%@", error.description);
    }];
    
    
}

- (IBAction)segControlClicked:(id)sender
{
    
    //Figures out the last clicked segment.
    int clickedSegment = [sender selectedSegmentIndex];
    if(clickedSegment == 0){
        if(self.pieChart.hidden){
            self.pieChart.hidden = NO;
        }
    }else{
        self.pieChart.hidden = YES;
    }
    
}

-(void)updateInfo:(FDataSnapshot *)snapshot {
    NSDictionary *json = snapshot.value[@"projects"][self.ProjectID][@"milestones"][self.MileStoneID];
    
    self.milestoneNameLabel.text = [json objectForKey:@"name"];
    self.dueDateLabel.text = [json objectForKey:@"due_date"];
    self.descriptionView.text = [json objectForKey:@"description"];
    self.tasksCompletedLabel.text = [NSString stringWithFormat:@"%@/%@", [json objectForKey:@"tasks_completed"],[json objectForKey:@"total_tasks"]];
    self.tasksCompleteBar.progress = [[json objectForKey:@"task_percent"] floatValue]/100;
    
    [UIApplication sharedApplication].networkActivityIndicatorVisible = NO;
    
    NSMutableArray *dataArray = [[NSMutableArray alloc] init];
    self.assignableDevs = snapshot.value[@"projects"][self.ProjectID][@"members"];
    self.allDevs = snapshot.value[@"users"];
    NSMutableArray *developers = [[NSMutableArray alloc] init];
    NSArray *keys = [self.assignableDevs allKeys];
    self.names = [[NSMutableArray alloc] init];
    for (NSString *key in keys) {
        NSNumber *lines = [[[[[[self.allDevs objectForKey:key] objectForKey:@"projects"] objectForKey:self.ProjectID] objectForKey:@"milestones"] objectForKey:self.MileStoneID] objectForKey:@"added_lines_of_code"];
        
        NSString *name = [[self.allDevs objectForKey:key] objectForKey:@"name"];
        if(name != NULL && lines!=0){
            [self.names addObject:name];
            [dataArray addObject:lines];
            [developers addObject:key];
        }
    }
    
    
    [self.pieChart renderInLayer:self.pieChart dataArray:dataArray nameArray:self.names];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
    HenryTasksTableViewController *vc = [segue destinationViewController];
    vc.ProjectID = self.ProjectID;
    vc.MileStoneID = self.MileStoneID;
    vc.milestoneName = self.milestoneNameLabel.text;
    vc.userTasks = self.userTasks;
    vc.uid = self.uid;
}

@end