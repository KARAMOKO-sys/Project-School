import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CourseFilter } from './course-filter';

describe('CourseFilter', () => {
  let component: CourseFilter;
  let fixture: ComponentFixture<CourseFilter>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CourseFilter]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CourseFilter);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
